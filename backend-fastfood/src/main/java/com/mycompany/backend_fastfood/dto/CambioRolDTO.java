package com.mycompany.backend_fastfood.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Asignación del rol del día (requisito de la consigna:
 * "Cada día serán asignados por los gerentes los roles
 * de vendedores y de cocineros").
 *
 * PUT /api/empleados/{id}/rol
 * { "rol": "COCINERO" }
 *
 * Solo el GERENTE puede usarlo, y solo entre VENDEDOR y COCINERO.
 */
@Getter
@Setter
@NoArgsConstructor
public class CambioRolDTO {

    /** Texto (no enum) para responder un mensaje claro si es inválido. */
    private String rol;
}