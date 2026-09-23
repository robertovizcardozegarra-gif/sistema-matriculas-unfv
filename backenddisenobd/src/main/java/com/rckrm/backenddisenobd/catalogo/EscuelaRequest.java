package com.rckrm.backenddisenobd.catalogo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record EscuelaRequest(
        @Positive Integer codigo,
        @NotBlank @Size(max = 150) String nombre,
        @Pattern(regexp = "(?i)A|I", message = "debe ser A o I") String estado) {
}
