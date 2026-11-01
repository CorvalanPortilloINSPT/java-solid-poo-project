package com.mycompany.backend_fastfood.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Compara contraseñas guardadas en texto plano, como las que cargó
 * el sistema de consola anterior.
 *
 * Solo se usa para VERIFICAR contraseñas viejas. Nunca para guardar:
 * las contraseñas nuevas se guardan con BCrypt (ver SecurityConfig).
 */
public class TextoPlanoLegacyEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        throw new UnsupportedOperationException(
                "No se guardan contraseñas en texto plano");
    }

    @Override
    public boolean matches(CharSequence rawPassword, String storedPassword) {

        if (rawPassword == null || storedPassword == null) {
            return false;
        }

        return MessageDigest.isEqual(
                rawPassword.toString().getBytes(StandardCharsets.UTF_8),
                storedPassword.getBytes(StandardCharsets.UTF_8));
    }
}