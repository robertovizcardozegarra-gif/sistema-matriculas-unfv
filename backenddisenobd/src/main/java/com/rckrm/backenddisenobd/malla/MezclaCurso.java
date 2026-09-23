package com.rckrm.backenddisenobd.malla;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "MezclaCurso")
public class MezclaCurso {

    @EmbeddedId
    private MezclaCursoId id;

    @Column(name = "TipoRequisito", length = 15, nullable = false)
    private String tipoRequisito;

    protected MezclaCurso() {
    }

    public MezclaCurso(MezclaCursoId id, String tipoRequisito) {
        this.id = id;
        this.tipoRequisito = tipoRequisito;
    }

    public MezclaCursoId getId() { return id; }
    public String getTipoRequisito() { return tipoRequisito; }
}
