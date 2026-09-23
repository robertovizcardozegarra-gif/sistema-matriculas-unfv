package com.rckrm.backenddisenobd.catalogo;

import com.rckrm.backenddisenobd.common.ConflictException;
import com.rckrm.backenddisenobd.common.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class CatalogoService {

    private final FacultadRepository facultades;
    private final EscuelaRepository escuelas;

    public CatalogoService(FacultadRepository facultades, EscuelaRepository escuelas) {
        this.facultades = facultades;
        this.escuelas = escuelas;
    }

    public List<FacultadDto> listarFacultades() {
        return facultades.findAllByOrderByNombreAsc().stream().map(FacultadDto::desde).toList();
    }

    public FacultadDto obtenerFacultad(Integer codigo) {
        return FacultadDto.desde(buscarFacultad(codigo));
    }

    @Transactional
    public FacultadDto crearFacultad(FacultadRequest request) {
        String nombre = limpiar(request.nombre());
        String sigla = normalizarOpcional(request.sigla());
        if (facultades.existsByNombreIgnoreCase(nombre)) {
            throw new ConflictException("Ya existe una facultad con ese nombre");
        }
        if (sigla != null && facultades.existsBySiglaIgnoreCase(sigla)) {
            throw new ConflictException("Ya existe una facultad con esa sigla");
        }
        Integer codigo = request.codigo() == null ? facultades.siguienteCodigo() : request.codigo();
        if (facultades.existsById(codigo)) {
            throw new ConflictException("Ya existe una facultad con el código " + codigo);
        }
        return FacultadDto.desde(facultades.save(
                new Facultad(codigo, sigla, nombre, request.codigoSede(), estado(request.estado()))));
    }

    @Transactional
    public FacultadDto actualizarFacultad(Integer codigo, FacultadRequest request) {
        Facultad facultad = buscarFacultad(codigo);
        facultad.actualizar(normalizarOpcional(request.sigla()), limpiar(request.nombre()),
                request.codigoSede(), estado(request.estado()));
        return FacultadDto.desde(facultades.save(facultad));
    }

    public List<EscuelaDto> listarEscuelas(Integer codigoFacultad) {
        buscarFacultad(codigoFacultad);
        return escuelas.findByIdCodigoFacultadOrderByNombreAsc(codigoFacultad).stream()
                .map(EscuelaDto::desde).toList();
    }

    @Transactional
    public EscuelaDto crearEscuela(Integer codigoFacultad, EscuelaRequest request) {
        buscarFacultad(codigoFacultad);
        String nombre = limpiar(request.nombre());
        if (escuelas.existsByIdCodigoFacultadAndNombreIgnoreCase(codigoFacultad, nombre)) {
            throw new ConflictException("Ya existe esa carrera en la facultad");
        }
        Integer codigoEscuela = request.codigo() == null
                ? escuelas.siguienteCodigo(codigoFacultad) : request.codigo();
        EscuelaId id = new EscuelaId(codigoFacultad, codigoEscuela);
        if (escuelas.existsById(id)) {
            throw new ConflictException("Ya existe una carrera con el código " + codigoEscuela);
        }
        return EscuelaDto.desde(escuelas.save(new Escuela(id, nombre, estado(request.estado()))));
    }

    @Transactional
    public EscuelaDto actualizarEscuela(Integer codigoFacultad, Integer codigoEscuela, EscuelaRequest request) {
        buscarFacultad(codigoFacultad);
        Escuela escuela = buscarEscuela(codigoFacultad, codigoEscuela);
        escuela.actualizar(limpiar(request.nombre()), estado(request.estado()));
        return EscuelaDto.desde(escuelas.save(escuela));
    }

    Facultad buscarFacultad(Integer codigo) {
        return facultades.findById(codigo)
                .orElseThrow(() -> new NotFoundException("Facultad no encontrada"));
    }

    public Escuela buscarEscuela(Integer codigoFacultad, Integer codigoEscuela) {
        return escuelas.findById(new EscuelaId(codigoFacultad, codigoEscuela))
                .orElseThrow(() -> new NotFoundException("Carrera no encontrada en la facultad indicada"));
    }

    private static String estado(String valor) {
        return valor == null || valor.isBlank() ? "A" : valor.trim().toUpperCase(Locale.ROOT);
    }

    private static String limpiar(String valor) {
        return valor == null ? null : valor.trim();
    }

    private static String normalizarOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim().toUpperCase(Locale.ROOT);
    }

    public record FacultadDto(Integer codigo, String sigla, String nombre, Integer codigoSede, String estado) {
        static FacultadDto desde(Facultad f) {
            return new FacultadDto(f.getCodigo(), f.getSigla(), f.getNombre(), f.getCodigoSede(), f.getEstado());
        }
    }

    public record EscuelaDto(Integer codigoFacultad, Integer codigo, String nombre, String estado) {
        static EscuelaDto desde(Escuela e) {
            return new EscuelaDto(e.getId().getCodigoFacultad(), e.getId().getCodigoEscuela(),
                    e.getNombre(), e.getEstado());
        }
    }
}
