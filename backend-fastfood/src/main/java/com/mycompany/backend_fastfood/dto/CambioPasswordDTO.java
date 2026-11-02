package com.mycompany.backend_fastfood.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Cambio de la contraseña propia (cualquier rol).
 *
 * PUT /api/auth/password
 * {
 *   "passwordActual": "admin123",
 *   "passwordNueva": "otraClave456"
 * }
 *
 * Se pide la contraseña actual para que, si alguien encuentra una
 * sesión abierta, no pueda cambiarla.
 */
@Getter
@Setter
@NoArgsConstructor
public class CambioPasswordDTO {

    private String passwordActual;
    private String passwordNueva;
}