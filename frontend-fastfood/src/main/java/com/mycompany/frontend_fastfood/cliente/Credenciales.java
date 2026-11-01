package com.mycompany.frontend_fastfood.cliente;

import java.io.Serializable;

/**
 * Usuario y contraseña del empleado logueado.
 *
 * Se guardan en la sesión HTTP del front después de un login exitoso
 * y se mandan al backend (HTTP Basic) en cada llamada.
 * El backend no guarda sesión: valida las credenciales en cada request.
 *
 * Serializable: la sesión de Tomcat puede guardar sus atributos en disco
 * (por ejemplo, al reiniciar), y para eso tienen que ser serializables.
 */
public record Credenciales(String usuario, String password) implements Serializable {
}