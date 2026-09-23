package com.rckrm.backenddisenobd.catalogo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record FacultadRequest(
        @Positive Integer codigo,
        @Size(max = 10) String sigla,
        @NotBlank @Size(max = 150) String nombre,
        @Positive Integer codigoSede,
        @Pattern(regexp = "(?i)A|I", message = "debe ser A o I") String estado) {
}
