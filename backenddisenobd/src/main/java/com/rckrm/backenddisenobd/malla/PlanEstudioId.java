package com.rckrm.backenddisenobd.malla;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PlanEstudioId implements Serializable {

    @Column(name = "CodFac")
    private Integer codigoFacultad;

    @Column(name = "CodEsc")
    private Integer codigoEscuela;

    @Column(name = "CorrPE")
    private Integer correlativo;

    protected PlanEstudioId() {
    }

    public PlanEstudioId(Integer codigoFacultad, Integer codigoEscuela, Integer correlativo) {
        this.codigoFacultad = codigoFacultad;
        this.codigoEscuela = codigoEscuela;
        this.correlativo = correlativo;
    }

    public Integer getCodigoFacultad() { return codigoFacultad; }
    public Integer getCodigoEscuela() { return codigoEscuela; }
    public Integer getCorrelativo() { return correlativo; }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof PlanEstudioId that)) return false;
        return Objects.equals(codigoFacultad, that.codigoFacultad)
                && Objects.equals(codigoEscuela, that.codigoEscuela)
                && Objects.equals(correlativo, that.correlativo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoFacultad, codigoEscuela, correlativo);
    }
}
