package com.mycompany.frontend_fastfood.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Empleado tal como lo devuelve el backend
 * (GET /api/empleados y GET /api/empleados/{id}).
 *
 * Mismos campos que EmpleadoDTO del backend. Nunca trae la contraseña.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmpleadoDTO implements Serializable {

    private String id;
    private String dni;
    private String nombre;
    private String apellido;
    private String usuario;
    private String rol;
    private boolean activo;
}