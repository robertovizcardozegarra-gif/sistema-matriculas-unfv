package com.rckrm.backenddisenobd.catalogo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Facultad")
public class Facultad {

    @Id
    @Column(name = "CodFac")
    private Integer codigo;

    @Column(name = "Sigla", length = 10, unique = true)
    private String sigla;

    @Column(name = "DenFac", length = 150, nullable = false, unique = true)
    private String nombre;

    @Column(name = "CodSede")
    private Integer codigoSede;

    @Column(name = "Estado", length = 1, nullable = false)
    private String estado = "A";

    protected Facultad() {
    }

    public Facultad(Integer codigo, String sigla, String nombre, Integer codigoSede, String estado) {
        this.codigo = codigo;
        actualizar(sigla, nombre, codigoSede, estado);
    }

    public void actualizar(String sigla, String nombre, Integer codigoSede, String estado) {
        this.sigla = sigla;
        this.nombre = nombre;
        this.codigoSede = codigoSede;
        this.estado = estado;
    }

    public Integer getCodigo() { return codigo; }
    public String getSigla() { return sigla; }
    public String getNombre() { return nombre; }
    public Integer getCodigoSede() { return codigoSede; }
    public String getEstado() { return estado; }
}
