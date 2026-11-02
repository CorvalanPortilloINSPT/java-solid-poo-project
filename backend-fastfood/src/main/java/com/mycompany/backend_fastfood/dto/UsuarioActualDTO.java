package com.mycompany.backend_fastfood.dto;

import com.mycompany.backend_fastfood.model.Empleado;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Datos del usuario que inició sesión. El front lo usa para saber
 * quién es y qué menú mostrar según el rol.
 */
@Getter
@AllArgsConstructor
public class UsuarioActualDTO {

    private String id;
    private String usuario;
    private String nombre;
    private String apellido;
    private String rol;

    public static UsuarioActualDTO desde(Empleado empleado) {
        return new UsuarioActualDTO(
                empleado.getID(),
                empleado.getUsuario(),
                empleado.getNombre(),
                empleado.getApellido(),
                empleado.getRol() != null
                        ? empleado.getRol().name()
                        : null
        );
    }
}