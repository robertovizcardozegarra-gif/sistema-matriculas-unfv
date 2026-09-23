package com.rckrm.backenddisenobd.seguridad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Rol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CodRol")
    private Integer codigo;

    @Column(name = "NombreRol", length = 30, nullable = false, unique = true)
    private String nombre;

    @Column(name = "Descripcion", length = 150)
    private String descripcion;

    @Column(name = "Estado", length = 1, nullable = false)
    private String estado = "A";

    protected Rol() {
    }

    public Rol(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Integer getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getEstado() { return estado; }
}
