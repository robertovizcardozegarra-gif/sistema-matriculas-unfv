package com.rckrm.backenddisenobd.malla;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record CursoRequest(
        @NotBlank @Size(max = 20) String codigoCurso,
        @NotBlank @Size(max = 180) String nombre,
        @Min(1) @Max(10) byte semestre,
        @Min(0) @Max(40) Byte horasTeoria,
        @Min(0) @Max(40) Byte horasPractica,
        @DecimalMin("1") @DecimalMax("99") BigDecimal creditos,
        @Pattern(regexp = "(?i)OBLIGATORIO|ELECTIVO|ACTIVIDAD|PRACTICA",
                message = "tipo de curso no válido") String tipo,
        @Pattern(regexp = "(?i)A|I", message = "debe ser A o I") String estado,
        List<@Valid PrerequisitoRequest> prerequisitos) {

    public record PrerequisitoRequest(
            @NotBlank @Size(max = 20) String codigoCurso,
            @Pattern(regexp = "(?i)OBLIGATORIO|RECOMENDADO",
                    message = "debe ser OBLIGATORIO o RECOMENDADO") String tipo) {
    }
}
