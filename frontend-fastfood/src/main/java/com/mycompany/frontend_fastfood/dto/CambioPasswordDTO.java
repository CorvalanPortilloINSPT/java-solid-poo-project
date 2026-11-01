package com.mycompany.frontend_fastfood.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Body del cambio de la propia contraseña (PUT /api/auth/password).
 *
 * Mismos campos que CambioPasswordDTO del backend:
 * { "passwordActual": "...", "passwordNueva": "..." }
 *
 * No se guarda en la sesión ni se escribe en los logs.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CambioPasswordDTO implements Serializable {

    private String passwordActual;
    private String passwordNueva;
}