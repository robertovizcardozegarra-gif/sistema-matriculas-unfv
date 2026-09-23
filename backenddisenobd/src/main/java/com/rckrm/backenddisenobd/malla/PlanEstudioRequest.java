package com.rckrm.backenddisenobd.malla;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PlanEstudioRequest(
        @Positive Integer correlativo,
        @Min(1900) @Max(2200) short anio,
        @NotBlank @Size(max = 150) String nombre,
        @Size(max = 100) String resolucion,
        LocalDate fechaAprobacion,
        LocalDate fechaInicioVigencia,
        LocalDate fechaFinVigencia,
        @Pattern(regexp = "(?i)BORRADOR|VIGENTE|NO_VIGENTE|ARCHIVADO",
                message = "debe ser BORRADOR, VIGENTE, NO_VIGENTE o ARCHIVADO") String estado) {
}
