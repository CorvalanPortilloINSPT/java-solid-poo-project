package com.mycompany.frontend_fastfood.dto;

import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Datos del usuario logueado, tal como los devuelve
 * GET /api/auth/me del backend (UsuarioActualDTO).
 *
 * Se guarda en la sesión junto con las credenciales: los JSP lo usan
 * para mostrar el nombre y elegir el menú según el rol.
 */
@Getter
@Setter
@NoArgsConstructor
public class UsuarioActualDTO implements Serializable {

    private String id;
    private String usuario;
    private String nombre;
    private String apellido;
    private String rol;
}