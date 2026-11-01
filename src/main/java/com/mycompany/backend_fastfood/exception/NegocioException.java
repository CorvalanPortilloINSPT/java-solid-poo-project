package com.mycompany.backend_fastfood.exception;

/**
 * Base de todos los errores de negocio del sistema.
 *
 * Los services lanzan subclases de esta excepción y no conocen HTTP.
 * El manejador global (GlobalExceptionHandler) traduce cada tipo
 * al código de respuesta correspondiente.
 *
 * Reemplaza a la interfaz ErrorNegocio del sistema anterior.
 */
public abstract class NegocioException extends RuntimeException {

    protected NegocioException(String mensaje) {
        super(mensaje);
    }
}