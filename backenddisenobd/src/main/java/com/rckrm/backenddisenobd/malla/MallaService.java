package com.rckrm.backenddisenobd.malla;

import com.rckrm.backenddisenobd.catalogo.CatalogoService;
import com.rckrm.backenddisenobd.common.ConflictException;
import com.rckrm.backenddisenobd.common.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

@Service
public class MallaService {

    private final CatalogoService catalogo;
    private final PlanEstudioRepository planes;
    private final CursoRepository cursos;
    private final MezclaCursoRepository prerequisitos;

    public MallaService(CatalogoService catalogo, PlanEstudioRepository planes,
                        CursoRepository cursos, MezclaCursoRepository prerequisitos) {
        this.catalogo = catalogo;
        this.planes = planes;
        this.cursos = cursos;
        this.prerequisitos = prerequisitos;
    }

    public List<PlanDto> listarPlanes(Integer facultad, Integer escuela) {
        catalogo.buscarEscuela(facultad, escuela);
        return planes.findByIdCodigoFacultadAndIdCodigoEscuelaOrderByAnioDesc(facultad, escuela)
                .stream().map(PlanDto::desde).toList();
    }

    public PlanDto obtenerPlan(Integer facultad, Integer escuela, Integer correlativo) {
        return PlanDto.desde(buscarPlan(facultad, escuela, correlativo));
    }

    @Transactional
    public PlanDto crearPlan(Integer facultad, Integer escuela, PlanEstudioRequest request) {
        catalogo.buscarEscuela(facultad, escuela);
        validarFechas(request.fechaInicioVigencia(), request.fechaFinVigencia());
        if (planes.existsByIdCodigoFacultadAndIdCodigoEscuelaAndAnio(facultad, escuela, request.anio())) {
            throw new ConflictException("Ya existe un plan del año " + request.anio() + " para esta carrera");
        }
        Integer correlativo = request.correlativo() == null
                ? planes.siguienteCorrelativo(facultad, escuela) : request.correlativo();
        PlanEstudioId id = new PlanEstudioId(facultad, escuela, correlativo);
        if (planes.existsById(id)) {
            throw new ConflictException("Ya existe un plan con el correlativo " + correlativo);
        }
        return PlanDto.desde(planes.save(new PlanEstudio(id, request.anio(), limpiar(request.nombre()),
                opcional(request.resolucion()), request.fechaAprobacion(), request.fechaInicioVigencia(),
                request.fechaFinVigencia(), normalizar(request.estado(), "BORRADOR"))));
    }

    @Transactional
    public PlanDto actualizarPlan(Integer facultad, Integer escuela, Integer correlativo,
                                  PlanEstudioRequest request) {
        PlanEstudio plan = buscarPlan(facultad, escuela, correlativo);
        validarFechas(request.fechaInicioVigencia(), request.fechaFinVigencia());
        plan.actualizar(request.anio(), limpiar(request.nombre()), opcional(request.resolucion()),
                request.fechaAprobacion(), request.fechaInicioVigencia(), request.fechaFinVigencia(),
                normalizar(request.estado(), "BORRADOR"));
        return PlanDto.desde(planes.save(plan));
    }

    public List<CursoDto> listarCursos(Integer facultad, Integer escuela, Integer correlativo) {
        buscarPlan(facultad, escuela, correlativo);
        return cursos.findByIdCodigoFacultadAndIdCodigoEscuelaAndIdCorrelativoPlanOrderBySemestreAscNombreAsc(
                        facultad, escuela, correlativo).stream()
                .map(curso -> convertirCurso(curso, cargarPrerequisitos(curso.getId())))
                .toList();
    }

    public CursoDto obtenerCurso(Integer facultad, Integer escuela, Integer correlativo, String codigoCurso) {
        Curso curso = buscarCurso(facultad, escuela, correlativo, codigoCurso);
        return convertirCurso(curso, cargarPrerequisitos(curso.getId()));
    }

    @Transactional
    public CursoDto crearCurso(Integer facultad, Integer escuela, Integer correlativo, CursoRequest request) {
        buscarPlan(facultad, escuela, correlativo);
        String codigo = normalizarCodigo(request.codigoCurso());
        CursoId id = new CursoId(facultad, escuela, correlativo, codigo);
        if (cursos.existsById(id)) {
            throw new ConflictException("Ya existe el curso " + codigo + " en este plan");
        }
        List<CursoRequest.PrerequisitoRequest> requisitos = lista(request.prerequisitos());
        validarPrerequisitos(id, requisitos);
        Curso curso = new Curso(id, limpiar(request.nombre()), request.semestre(), request.horasTeoria(),
                request.horasPractica(), creditos(request.creditos()), normalizar(request.tipo(), "OBLIGATORIO"),
                requisitos.isEmpty() ? "N" : "S", normalizar(request.estado(), "A"));
        cursos.save(curso);
        guardarPrerequisitos(id, requisitos);
        return convertirCurso(curso, cargarPrerequisitos(id));
    }

    @Transactional
    public List<CursoDto> crearCursos(Integer facultad, Integer escuela, Integer correlativo,
                                      List<CursoRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("Debes enviar al menos un curso");
        }
        if (requests.size() > 20) {
            throw new IllegalArgumentException("Solo se pueden registrar hasta 20 cursos por operación");
        }
        return requests.stream()
                .map(request -> crearCurso(facultad, escuela, correlativo, request))
                .toList();
    }

    @Transactional
    public CursoDto actualizarCurso(Integer facultad, Integer escuela, Integer correlativo, String codigoCurso,
                                    CursoRequest request) {
        String codigo = normalizarCodigo(codigoCurso);
        if (!codigo.equals(normalizarCodigo(request.codigoCurso()))) {
            throw new IllegalArgumentException("El código del cuerpo debe coincidir con el de la URL");
        }
        Curso curso = buscarCurso(facultad, escuela, correlativo, codigo);
        List<CursoRequest.PrerequisitoRequest> requisitos = lista(request.prerequisitos());
        validarPrerequisitos(curso.getId(), requisitos);
        curso.actualizar(limpiar(request.nombre()), request.semestre(), request.horasTeoria(),
                request.horasPractica(), creditos(request.creditos()), normalizar(request.tipo(), "OBLIGATORIO"),
                requisitos.isEmpty() ? "N" : "S", normalizar(request.estado(), "A"));
        cursos.save(curso);
        prerequisitos.deleteByIdCodigoFacultadAndIdCodigoEscuelaAndIdCorrelativoPlanAndIdCodigoCurso(
                facultad, escuela, correlativo, codigo);
        guardarPrerequisitos(curso.getId(), requisitos);
        return convertirCurso(curso, cargarPrerequisitos(curso.getId()));
    }

    @Transactional
    public void eliminarCurso(Integer facultad, Integer escuela, Integer correlativo, String codigoCurso) {
        Curso curso = buscarCurso(facultad, escuela, correlativo, codigoCurso);
        String codigo = curso.getId().getCodigoCurso();
        prerequisitos.deleteRelacionesCurso(facultad, escuela, correlativo, codigo);
        prerequisitos.flush();
        cursos.delete(curso);
        cursos.flush();
    }

    private PlanEstudio buscarPlan(Integer facultad, Integer escuela, Integer correlativo) {
        return planes.findById(new PlanEstudioId(facultad, escuela, correlativo))
                .orElseThrow(() -> new NotFoundException("Plan de estudios no encontrado"));
    }

    private Curso buscarCurso(Integer facultad, Integer escuela, Integer correlativo, String codigoCurso) {
        return cursos.findById(new CursoId(facultad, escuela, correlativo, normalizarCodigo(codigoCurso)))
                .orElseThrow(() -> new NotFoundException("Curso no encontrado en el plan indicado"));
    }

    private void validarPrerequisitos(CursoId curso, List<CursoRequest.PrerequisitoRequest> requisitos) {
        var unicos = new LinkedHashMap<String, CursoRequest.PrerequisitoRequest>();
        for (var requisito : requisitos) {
            String codigo = normalizarCodigo(requisito.codigoCurso());
            if (codigo.equals(curso.getCodigoCurso())) {
                throw new IllegalArgumentException("Un curso no puede ser su propio prerrequisito");
            }
            if (unicos.put(codigo, requisito) != null) {
                throw new IllegalArgumentException("El prerrequisito " + codigo + " está repetido");
            }
            CursoId requisitoId = new CursoId(curso.getCodigoFacultad(), curso.getCodigoEscuela(),
                    curso.getCorrelativoPlan(), codigo);
            if (!cursos.existsById(requisitoId)) {
                throw new IllegalArgumentException("No existe el curso prerrequisito " + codigo + " en este plan");
            }
        }
    }

    private void guardarPrerequisitos(CursoId curso, List<CursoRequest.PrerequisitoRequest> requisitos) {
        var entidades = requisitos.stream().map(requisito -> new MezclaCurso(
                new MezclaCursoId(curso.getCodigoFacultad(), curso.getCodigoEscuela(), curso.getCorrelativoPlan(),
                        curso.getCodigoCurso(), normalizarCodigo(requisito.codigoCurso())),
                normalizar(requisito.tipo(), "OBLIGATORIO"))).toList();
        prerequisitos.saveAll(entidades);
    }

    private List<PrerequisitoDto> cargarPrerequisitos(CursoId id) {
        return prerequisitos
                .findByIdCodigoFacultadAndIdCodigoEscuelaAndIdCorrelativoPlanAndIdCodigoCurso(
                        id.getCodigoFacultad(), id.getCodigoEscuela(), id.getCorrelativoPlan(), id.getCodigoCurso())
                .stream().map(p -> new PrerequisitoDto(p.getId().getCodigoCursoPrerequisito(), p.getTipoRequisito()))
                .toList();
    }

    private static CursoDto convertirCurso(Curso curso, List<PrerequisitoDto> requisitos) {
        CursoId id = curso.getId();
        return new CursoDto(id.getCodigoFacultad(), id.getCodigoEscuela(), id.getCorrelativoPlan(),
                id.getCodigoCurso(), curso.getNombre(), curso.getSemestre(), curso.getHorasTeoria(),
                curso.getHorasPractica(), curso.getCreditos(), curso.getTipo(), curso.getGrupoElectivo(),
                curso.getEstado(), requisitos);
    }

    private static void validarFechas(LocalDate inicio, LocalDate fin) {
        if (inicio != null && fin != null && fin.isBefore(inicio)) {
            throw new IllegalArgumentException("La fecha fin de vigencia no puede ser anterior a la fecha de inicio");
        }
    }

    private static BigDecimal creditos(BigDecimal valor) {
        if (valor == null) throw new IllegalArgumentException("Los créditos son obligatorios");
        if (valor.stripTrailingZeros().scale() > 0) {
            throw new IllegalArgumentException("Los créditos deben ser un número entero");
        }
        return valor;
    }

    private static String limpiar(String valor) { return valor == null ? null : valor.trim(); }
    private static String opcional(String valor) { return valor == null || valor.isBlank() ? null : valor.trim(); }
    private static String normalizarCodigo(String valor) { return limpiar(valor).toUpperCase(Locale.ROOT); }
    private static String normalizar(String valor, String defecto) {
        return valor == null || valor.isBlank() ? defecto : valor.trim().toUpperCase(Locale.ROOT);
    }
    private static <T> List<T> lista(List<T> valores) { return valores == null ? List.of() : valores; }

    public record PlanDto(Integer codigoFacultad, Integer codigoEscuela, Integer correlativo, Short anio,
                          String nombre, String resolucion, LocalDate fechaAprobacion,
                          LocalDate fechaInicioVigencia, LocalDate fechaFinVigencia, String estado) {
        static PlanDto desde(PlanEstudio p) {
            return new PlanDto(p.getId().getCodigoFacultad(), p.getId().getCodigoEscuela(),
                    p.getId().getCorrelativo(), p.getAnio(), p.getNombre(), p.getResolucion(),
                    p.getFechaAprobacion(), p.getFechaInicioVigencia(), p.getFechaFinVigencia(), p.getEstado());
        }
    }

    public record PrerequisitoDto(String codigoCurso, String tipo) {
    }

    public record CursoDto(Integer codigoFacultad, Integer codigoEscuela, Integer correlativoPlan,
                           String codigoCurso, String nombre, Byte semestre, Byte horasTeoria,
                           Byte horasPractica, BigDecimal creditos, String tipo, String grupoElectivo, String estado,
                           List<PrerequisitoDto> prerequisitos) {
    }
}
