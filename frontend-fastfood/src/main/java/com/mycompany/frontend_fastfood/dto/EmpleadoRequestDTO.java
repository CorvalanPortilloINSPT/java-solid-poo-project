package com.mycompany.frontend_fastfood.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Body que el front envía al backend para dar de alta o modificar
 * un empleado (POST /api/empleados y PUT /api/empleados/{id}).
 *
 * Mismos campos que EmpleadoRequestDTO del backend.
 *
 * Alta: todos los campos.
 * Modificación: dni y rol van en null (el backend no los cambia por
 * esta vía) y password en null si se deja vacía (se mantiene la actual).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoRequestDTO implements Serializable {

    private String dni;
    private String nombre;
    private String apellido;
    private String usuario;
    private String password;
    private String rol;
}