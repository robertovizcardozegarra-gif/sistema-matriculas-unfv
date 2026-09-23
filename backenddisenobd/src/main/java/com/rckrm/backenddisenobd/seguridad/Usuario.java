package com.rckrm.backenddisenobd.seguridad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "Usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CodUsuario")
    private Long codigo;

    @Column(name = "NombreUsuario", length = 60, nullable = false, unique = true)
    private String nombreUsuario;

    @Column(name = "ClaveHash", length = 255, nullable = false)
    private String claveHash;

    @Column(name = "Correo", length = 150, unique = true)
    private String correo;

    @Column(name = "Nombres", length = 100)
    private String nombres;

    @Column(name = "Apellidos", length = 100)
    private String apellidos;

    @Column(name = "Estado", length = 1, nullable = false)
    private String estado = "A";

    @Column(name = "IntentosFallidos", nullable = false)
    private Byte intentosFallidos = 0;

    @Column(name = "BloqueadoHasta")
    private LocalDateTime bloqueadoHasta;

    @Column(name = "FechaCreacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "UltimoAcceso")
    private LocalDateTime ultimoAcceso;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "UsuarioRol",
            joinColumns = @JoinColumn(name = "CodUsuario"),
            inverseJoinColumns = @JoinColumn(name = "CodRol"))
    private Set<Rol> roles = new LinkedHashSet<>();

    protected Usuario() {
    }

    public Usuario(String nombreUsuario, String claveHash, String correo, String nombres, String apellidos) {
        this.nombreUsuario = nombreUsuario;
        this.claveHash = claveHash;
        this.correo = correo;
        this.nombres = nombres;
        this.apellidos = apellidos;
    }

    public void agregarRol(Rol rol) { roles.add(rol); }
    public void registrarAcceso() { ultimoAcceso = LocalDateTime.now(); }

    public Long getCodigo() { return codigo; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getClaveHash() { return claveHash; }
    public String getCorreo() { return correo; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getEstado() { return estado; }
    public Byte getIntentosFallidos() { return intentosFallidos; }
    public LocalDateTime getBloqueadoHasta() { return bloqueadoHasta; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getUltimoAcceso() { return ultimoAcceso; }
    public Set<Rol> getRoles() { return roles; }
}
