package com.mycompany.backend_fastfood.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Empleado")
public class Empleado extends Persona {

    private boolean activo;

    protected Empleado() {
        // Constructor requerido por JPA
    }

    /**
     * Crea un empleado nuevo, siempre activo.
     * La contraseña tiene que llegar ya codificada (BCrypt);
     * de eso se encarga EmpleadoService.
     */
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