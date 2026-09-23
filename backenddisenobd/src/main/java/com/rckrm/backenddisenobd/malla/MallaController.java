package com.rckrm.backenddisenobd.malla;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/facultades/{codigoFacultad}/escuelas/{codigoEscuela}/planes")
public class MallaController {

    private final MallaService service;

    public MallaController(MallaService service) {
        this.service = service;
    }

    @GetMapping
    public List<MallaService.PlanDto> listarPlanes(@PathVariable Integer codigoFacultad,
                                                   @PathVariable Integer codigoEscuela) {
        return service.listarPlanes(codigoFacultad, codigoEscuela);
    }

    @GetMapping("/{correlativo}")
    public MallaService.PlanDto obtenerPlan(@PathVariable Integer codigoFacultad,
                                            @PathVariable Integer codigoEscuela,
                                            @PathVariable Integer correlativo) {
        return service.obtenerPlan(codigoFacultad, codigoEscuela, correlativo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MallaService.PlanDto crearPlan(@PathVariable Integer codigoFacultad,
                                          @PathVariable Integer codigoEscuela,
                                          @Valid @RequestBody PlanEstudioRequest request) {
        return service.crearPlan(codigoFacultad, codigoEscuela, request);
    }

    @PutMapping("/{correlativo}")
    public MallaService.PlanDto actualizarPlan(@PathVariable Integer codigoFacultad,
                                               @PathVariable Integer codigoEscuela,
                                               @PathVariable Integer correlativo,
                                               @Valid @RequestBody PlanEstudioRequest request) {
        return service.actualizarPlan(codigoFacultad, codigoEscuela, correlativo, request);
    }

    @GetMapping("/{correlativo}/cursos")
    public List<MallaService.CursoDto> listarCursos(@PathVariable Integer codigoFacultad,
                                                    @PathVariable Integer codigoEscuela,
                                                    @PathVariable Integer correlativo) {
        return service.listarCursos(codigoFacultad, codigoEscuela, correlativo);
    }

    @GetMapping("/{correlativo}/cursos/{codigoCurso}")
    public MallaService.CursoDto obtenerCurso(@PathVariable Integer codigoFacultad,
                                              @PathVariable Integer codigoEscuela,
                                              @PathVariable Integer correlativo,
                                              @PathVariable String codigoCurso) {
        return service.obtenerCurso(codigoFacultad, codigoEscuela, correlativo, codigoCurso);
    }

    @PostMapping("/{correlativo}/cursos")
    @ResponseStatus(HttpStatus.CREATED)
    public MallaService.CursoDto crearCurso(@PathVariable Integer codigoFacultad,
                                            @PathVariable Integer codigoEscuela,
                                            @PathVariable Integer correlativo,
                                            @Valid @RequestBody CursoRequest request) {
        return service.crearCurso(codigoFacultad, codigoEscuela, correlativo, request);
    }

    @PostMapping("/{correlativo}/cursos/lote")
    @ResponseStatus(HttpStatus.CREATED)
    public List<MallaService.CursoDto> crearCursos(
            @PathVariable Integer codigoFacultad,
            @PathVariable Integer codigoEscuela,
            @PathVariable Integer correlativo,
            @RequestBody @NotEmpty @Size(max = 20) List<@Valid CursoRequest> requests) {
        return service.crearCursos(codigoFacultad, codigoEscuela, correlativo, requests);
    }

    @PutMapping("/{correlativo}/cursos/{codigoCurso}")
    public MallaService.CursoDto actualizarCurso(@PathVariable Integer codigoFacultad,
                                                 @PathVariable Integer codigoEscuela,
                                                 @PathVariable Integer correlativo,
                                                 @PathVariable String codigoCurso,
                                                 @Valid @RequestBody CursoRequest request) {
        return service.actualizarCurso(codigoFacultad, codigoEscuela, correlativo, codigoCurso, request);
    }

    @DeleteMapping("/{correlativo}/cursos/{codigoCurso}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarCurso(@PathVariable Integer codigoFacultad,
                              @PathVariable Integer codigoEscuela,
                              @PathVariable Integer correlativo,
                              @PathVariable String codigoCurso) {
        service.eliminarCurso(codigoFacultad, codigoEscuela, correlativo, codigoCurso);
    }
}
