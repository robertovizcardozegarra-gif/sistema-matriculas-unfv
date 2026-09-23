package com.rckrm.backenddisenobd.catalogo;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "Escuela")
public class Escuela {

    @EmbeddedId
    private EscuelaId id;

    @Column(name = "DenEscuela", length = 150, nullable = false)
    private String nombre;

    @Column(name = "Estado", length = 1, nullable = false)
    private String estado = "A";

    protected Escuela() {
    }

    public Escuela(EscuelaId id, String nombre, String estado) {
        this.id = id;
        actualizar(nombre, estado);
    }

    public void actualizar(String nombre, String estado) {
        this.nombre = nombre;
        this.estado = estado;
    }

    public EscuelaId getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEstado() { return estado; }
}
