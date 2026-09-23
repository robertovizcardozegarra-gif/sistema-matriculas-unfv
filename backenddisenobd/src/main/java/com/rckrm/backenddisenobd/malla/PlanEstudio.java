package com.rckrm.backenddisenobd.malla;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "PlanEstudio")
public class PlanEstudio {

    @EmbeddedId
    private PlanEstudioId id;

    @Column(name = "AnioPE", nullable = false)
    private Short anio;

    @Column(name = "DenPlan", length = 150, nullable = false)
    private String nombre;

    @Column(name = "Resolucion", length = 100)
    private String resolucion;

    @Column(name = "FechaAprobacion")
    private LocalDate fechaAprobacion;

    @Column(name = "FechaInicioVigencia")
    private LocalDate fechaInicioVigencia;

    @Column(name = "FechaFinVigencia")
    private LocalDate fechaFinVigencia;

    @Column(name = "Estado", length = 15, nullable = false)
    private String estado = "BORRADOR";

    protected PlanEstudio() {
    }

    public PlanEstudio(PlanEstudioId id, Short anio, String nombre, String resolucion,
                       LocalDate fechaAprobacion, LocalDate fechaInicioVigencia,
                       LocalDate fechaFinVigencia, String estado) {
        this.id = id;
        actualizar(anio, nombre, resolucion, fechaAprobacion, fechaInicioVigencia, fechaFinVigencia, estado);
    }

    public void actualizar(Short anio, String nombre, String resolucion, LocalDate fechaAprobacion,
                           LocalDate fechaInicioVigencia, LocalDate fechaFinVigencia, String estado) {
        this.anio = anio;
        this.nombre = nombre;
        this.resolucion = resolucion;
        this.fechaAprobacion = fechaAprobacion;
        this.fechaInicioVigencia = fechaInicioVigencia;
        this.fechaFinVigencia = fechaFinVigencia;
        this.estado = estado;
    }

    public PlanEstudioId getId() { return id; }
    public Short getAnio() { return anio; }
    public String getNombre() { return nombre; }
    public String getResolucion() { return resolucion; }
    public LocalDate getFechaAprobacion() { return fechaAprobacion; }
    public LocalDate getFechaInicioVigencia() { return fechaInicioVigencia; }
    public LocalDate getFechaFinVigencia() { return fechaFinVigencia; }
    public String getEstado() { return estado; }
}
