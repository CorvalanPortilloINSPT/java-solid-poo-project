package com.mycompany.frontend_fastfood.cliente;

/**
 * Error al llamar al backend.
 *
 * Lleva el código HTTP y un mensaje listo para mostrar en la página:
 * - 400, 404, 409 y 403 con detalle: el "detail" que devuelve el backend
 *   (por ejemplo "Ítem 2: no se puede quitar Pan porque es obligatorio").
 * - 401: usuario o contraseña incorrectos (o empleado dado de baja).
 * - 403 sin detalle: el rol no tiene permiso para esa operación.
 * - 0: no se pudo conectar con el backend (apagado o en otro puerto).
 *
 * Es no checked (RuntimeException), igual que las excepciones del backend:
 * los controladores la capturan donde quieren mostrar el mensaje.
 */
public class BackendException extends RuntimeException {

    /** Código usado cuando no hubo respuesta del backend. */
    public static final int SIN_CONEXION = 0;

    private final int status;

    public BackendException(int status, String mensaje) {
        super(mensaje);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    /** Credenciales inválidas: hay que volver al login. */
    public boolean isNoAutenticado() {
        return status == 401;
    }

    public boolean isSinConexion() {
        return status == SIN_CONEXION;
    }
}