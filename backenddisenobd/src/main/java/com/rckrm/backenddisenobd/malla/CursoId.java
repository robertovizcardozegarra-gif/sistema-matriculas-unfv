package com.rckrm.backenddisenobd.malla;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CursoId implements Serializable {

    @Column(name = "CodFac")
    private Integer codigoFacultad;

    @Column(name = "CodEsc")
    private Integer codigoEscuela;

    @Column(name = "CorrPE")
    private Integer correlativoPlan;

    @Column(name = "CodCurso", length = 20)
    private String codigoCurso;

    protected CursoId() {
    }

    public CursoId(Integer codigoFacultad, Integer codigoEscuela, Integer correlativoPlan, String codigoCurso) {
        this.codigoFacultad = codigoFacultad;
        this.codigoEscuela = codigoEscuela;
        this.correlativoPlan = correlativoPlan;
        this.codigoCurso = codigoCurso;
    }

    public Integer getCodigoFacultad() { return codigoFacultad; }
    public Integer getCodigoEscuela() { return codigoEscuela; }
    public Integer getCorrelativoPlan() { return correlativoPlan; }
    public String getCodigoCurso() { return codigoCurso; }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof CursoId that)) return false;
        return Objects.equals(codigoFacultad, that.codigoFacultad)
                && Objects.equals(codigoEscuela, that.codigoEscuela)
                && Objects.equals(correlativoPlan, that.correlativoPlan)
                && Objects.equals(codigoCurso, that.codigoCurso);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoFacultad, codigoEscuela, correlativoPlan, codigoCurso);
    }
}
