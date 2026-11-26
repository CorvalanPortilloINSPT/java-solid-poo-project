package com.mycompany.backend_fastfood.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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

/**
 * Datos comunes de las personas del sistema.
 *
 * La API devuelve la entidad directamente (sin DTO). Las anotaciones de
 * Jackson definen cómo se ve en el JSON:
 * - "id" y "dni" en minúscula (el front los espera así).
 * - La contraseña se RECIBE en el alta y en la modificación (con el
 *   nombre "password"), pero NUNCA se devuelve en una respuesta.
 */
@Entity
@Table(name = "persona")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "DTYPE",
        discriminatorType = DiscriminatorType.STRING
)
public abstract class Persona {

    @Id
    @JsonProperty("id")
    private String ID;

    /** No tiene setter: @JsonProperty permite recibirlo en el JSON del alta. */
    @JsonProperty("dni")
    private String DNI;

    private String nombre;
    private String apellido;
    private String usuario;

    /**
     * Guarda el hash BCrypt, nunca la contraseña.
     * WRITE_ONLY: Jackson la lee del JSON que llega, pero no la escribe
     * en el JSON que sale.
     */
    @Column(name = "contraseña")
    @JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY)
    private String contraseña;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    protected Persona() {
        // Constructor requerido por JPA (y por Jackson al leer un JSON)
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

    /** No sale en el JSON: lo impide el WRITE_ONLY del atributo. */
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
