package com.mycompany.frontend_fastfood.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Body del rol del día (PUT /api/empleados/{id}/rol).
 *
 * Mismo campo que CambioRolDTO del backend:
 * { "rol": "COCINERO" }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CambioRolDTO implements Serializable {

    private String rol;
}