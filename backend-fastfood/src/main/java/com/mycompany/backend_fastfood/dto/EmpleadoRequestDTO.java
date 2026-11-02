package com.mycompany.backend_fastfood.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Datos que envía el cliente para dar de alta o modificar un empleado.
 *
 * Alta (POST): todos los campos son obligatorios.
 * {
 *   "dni": "30111222",
 *   "nombre": "Ana",
 *   "apellido": "Gomez",
 *   "usuario": "agomez",
 *   "password": "secreta123",
 *   "rol": "VENDEDOR"
 * }
 *
 * Modificación (PUT): nombre, apellido y usuario obligatorios;
 * password es opcional (si no se envía, se mantiene la actual).
 * El DNI y el rol no se modifican por esta vía: si se envían con
 * un valor distinto al actual, se rechaza el pedido.
 * El rol del día se cambia con PUT /api/empleados/{id}/rol.
 */
@Getter
@Setter
@NoArgsConstructor
public class EmpleadoRequestDTO {

    private String dni;
    private String nombre;
    private String apellido;
    private String usuario;
    private String password;

    /** Texto (no enum) para poder responder un mensaje claro si es inválido. */
    private String rol;
}