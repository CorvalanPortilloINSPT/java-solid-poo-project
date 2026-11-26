package com.mycompany.frontend_fastfood.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Body del rol del día (PUT /api/empleados/{id}/rol).
 *
 * Mismo formato que espera el backend (toma el rol de la entidad Empleado):
 * { "rol": "COCINERO" }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CambioRolDTO implements Serializable {

    private String rol;
}