package com.mycompany.backend_fastfood.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Empleado")
public class Empleado extends Persona {

    private boolean activo;

    protected Empleado() {
        // Constructor requerido por JPA (y por Jackson al leer un JSON)
    }

    /**
     * Crea un empleado nuevo, siempre activo.
     * La contraseña tiene que llegar ya codificada (BCrypt);
     * de eso se encarga EmpleadoService.
     *
     * Jackson NO usa este constructor (Mode.DISABLED): al leer un JSON usa
     * el constructor vacío y carga cada dato con su anotación o su setter
     * (por ejemplo "dni" y "password", ver Persona). El empleado que se
     * guarda lo arma EmpleadoService con este constructor.
     */
    @JsonCreator(mode = JsonCreator.Mode.DISABLED)
    public Empleado(
            String ID,
            Rol rol,
            String DNI,
            String nombre,
            String apellido,
            String usuario,
            String contraseña) {

        super(ID, rol, DNI, nombre, apellido, usuario, contraseña);
        this.activo = true;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}