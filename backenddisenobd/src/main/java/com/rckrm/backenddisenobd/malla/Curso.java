package com.rckrm.backenddisenobd.malla;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "Curso")
public class Curso {

    @EmbeddedId
    private CursoId id;

    @Column(name = "DenCurso", length = 180, nullable = false)
    private String nombre;

    @Column(name = "Semestre", nullable = false)
    private Byte semestre;

    @Column(name = "HT")
    private Byte horasTeoria;

    @Column(name = "HP")
    private Byte horasPractica;

    @Column(name = "Cred", precision = 4, scale = 1, nullable = false)
    private BigDecimal creditos;

    @Column(name = "TipoCurso", length = 15, nullable = false)
    private String tipo;

    @Column(name = "CodGrupoElectivo", length = 20)
    private String grupoElectivo;

    @Column(name = "PreReq", length = 1, nullable = false)
    private String tienePrerequisito = "N";

    @Column(name = "Estado", length = 1, nullable = false)
    private String estado = "A";

    protected Curso() {
    }

    public Curso(CursoId id, String nombre, Byte semestre, Byte horasTeoria, Byte horasPractica,
                 BigDecimal creditos, String tipo, String tienePrerequisito, String estado) {
        this.id = id;
        actualizar(nombre, semestre, horasTeoria, horasPractica, creditos, tipo, tienePrerequisito, estado);
    }

    public void actualizar(String nombre, Byte semestre, Byte horasTeoria, Byte horasPractica,
                           BigDecimal creditos, String tipo, String tienePrerequisito, String estado) {
        this.nombre = nombre;
        this.semestre = semestre;
        this.horasTeoria = horasTeoria;
        this.horasPractica = horasPractica;
        this.creditos = creditos;
        this.tipo = tipo;
        this.tienePrerequisito = tienePrerequisito;
        this.estado = estado;
    }

    public CursoId getId() { return id; }
    public String getNombre() { return nombre; }
    public Byte getSemestre() { return semestre; }
    public Byte getHorasTeoria() { return horasTeoria; }
    public Byte getHorasPractica() { return horasPractica; }
    public BigDecimal getCreditos() { return creditos; }
    public String getTipo() { return tipo; }
    public String getGrupoElectivo() { return grupoElectivo; }
    public String getTienePrerequisito() { return tienePrerequisito; }
    public String getEstado() { return estado; }
}
