package com.rckrm.backenddisenobd.catalogo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EscuelaId implements Serializable {

    @Column(name = "CodFac")
    private Integer codigoFacultad;

    @Column(name = "CodEsc")
    private Integer codigoEscuela;

    protected EscuelaId() {
    }

    public EscuelaId(Integer codigoFacultad, Integer codigoEscuela) {
        this.codigoFacultad = codigoFacultad;
        this.codigoEscuela = codigoEscuela;
    }

    public Integer getCodigoFacultad() { return codigoFacultad; }
    public Integer getCodigoEscuela() { return codigoEscuela; }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof EscuelaId that)) return false;
        return Objects.equals(codigoFacultad, that.codigoFacultad)
                && Objects.equals(codigoEscuela, that.codigoEscuela);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoFacultad, codigoEscuela);
    }
}
