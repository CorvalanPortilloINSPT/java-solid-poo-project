package com.mycompany.backend_fastfood.exception;

/**
 * Se lanza cuando un dato de entrada falta o no es válido
 * (descripción vacía, precio negativo, cantidad cero, etc.).
 * Se responde con HTTP 400.
 */
public class DatoInvalidoException extends NegocioException {

    public DatoInvalidoException(String mensaje) {
        super(mensaje);
    }
}