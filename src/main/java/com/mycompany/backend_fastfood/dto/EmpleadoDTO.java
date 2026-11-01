package com.mycompany.backend_fastfood.dto;

import com.mycompany.backend_fastfood.model.Empleado;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Datos de un empleado que devuelve la API.
 * Nunca incluye la contraseña.
 */
@Getter
@AllArgsConstructor
public class EmpleadoDTO {

    private String id;
    private String dni;
    private String nombre;
    private String apellido;
    private String usuario;
    private String rol;
    private boolean activo;

    public static EmpleadoDTO desde(Empleado empleado) {
        return new EmpleadoDTO(
                empleado.getID(),
                empleado.getDNI(),
                empleado.getNombre(),
                empleado.getApellido(),
                empleado.getUsuario(),
                empleado.getRol() != null
                        ? empleado.getRol().name()
                        : null,
                empleado.isActivo()
        );
    }
}