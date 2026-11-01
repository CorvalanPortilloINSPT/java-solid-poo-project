package com.mycompany.backend_fastfood.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity
@Table(name = "persona")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "DTYPE",
        discriminatorType = DiscriminatorType.STRING
)
public abstract class Persona {

    @Id
    private String ID;

    private String DNI;
    private String nombre;
    private String apellido;
    private String usuario;

    @Column(name = "contraseña")
    private String contraseña;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    protected Persona() {
        // Constructor requerido por JPA
    }

    protected Persona(
            String ID,
            Rol rol,
            String DNI,
            String nombre,
            String apellido,
            String usuario,
            String contraseña) {

        this.ID = ID;
        this.rol = rol;
        this.DNI = DNI;
        this.nombre = nombre;
        this.apellido = apellido;
        this.usuario = usuario;
        this.contraseña = contraseña;
    }

    public String getID() {
        return ID;
    }

    public String getDNI() {
        return DNI;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getUsuario() {
        return usuario;
    }

    @JsonIgnore
    public String getContraseña() {
        return contraseña;
    }

    public Rol getRol() {
        return rol;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /** Lo usa el gerente para asignar el rol del día (vendedor/cocinero). */
    public void setRol(Rol rol) {
        this.rol = rol;
    }
}