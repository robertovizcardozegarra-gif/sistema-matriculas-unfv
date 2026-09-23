package com.rckrm.backenddisenobd.catalogo;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/facultades")
public class CatalogoController {

    private final CatalogoService service;

    public CatalogoController(CatalogoService service) {
        this.service = service;
    }

    @GetMapping
    public List<CatalogoService.FacultadDto> listarFacultades() {
        return service.listarFacultades();
    }

    @GetMapping("/{codigoFacultad}")
    public CatalogoService.FacultadDto obtenerFacultad(@PathVariable Integer codigoFacultad) {
        return service.obtenerFacultad(codigoFacultad);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CatalogoService.FacultadDto crearFacultad(@Valid @RequestBody FacultadRequest request) {
        return service.crearFacultad(request);
    }

    @PutMapping("/{codigoFacultad}")
    public CatalogoService.FacultadDto actualizarFacultad(@PathVariable Integer codigoFacultad,
                                                          @Valid @RequestBody FacultadRequest request) {
        return service.actualizarFacultad(codigoFacultad, request);
    }

    @GetMapping("/{codigoFacultad}/escuelas")
    public List<CatalogoService.EscuelaDto> listarEscuelas(@PathVariable Integer codigoFacultad) {
        return service.listarEscuelas(codigoFacultad);
    }

    @PostMapping("/{codigoFacultad}/escuelas")
    @ResponseStatus(HttpStatus.CREATED)
    public CatalogoService.EscuelaDto crearEscuela(@PathVariable Integer codigoFacultad,
                                                   @Valid @RequestBody EscuelaRequest request) {
        return service.crearEscuela(codigoFacultad, request);
    }

    @PutMapping("/{codigoFacultad}/escuelas/{codigoEscuela}")
    public CatalogoService.EscuelaDto actualizarEscuela(@PathVariable Integer codigoFacultad,
                                                        @PathVariable Integer codigoEscuela,
                                                        @Valid @RequestBody EscuelaRequest request) {
        return service.actualizarEscuela(codigoFacultad, codigoEscuela, request);
    }
}
