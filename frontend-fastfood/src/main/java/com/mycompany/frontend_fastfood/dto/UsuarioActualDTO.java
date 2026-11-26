package com.mycompany.frontend_fastfood.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Datos del usuario logueado, tal como los devuelve
 * GET /api/auth/me del backend (la entidad Empleado, sin la contraseña).
 * El backend manda además "dni" y "activo": ignoreUnknown los descarta,
 * porque esta clase solo guarda lo que usa la sesión.
 *
 * Se guarda en la sesión junto con las credenciales: los JSP lo usan
 * para mostrar el nombre y elegir el menú según el rol.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsuarioActualDTO implements Serializable {

    private String id;
    private String usuario;
    private String nombre;
    private String apellido;
    private String rol;
}