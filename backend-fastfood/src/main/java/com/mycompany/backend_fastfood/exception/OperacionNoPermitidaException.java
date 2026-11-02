package com.mycompany.backend_fastfood.exception;

/**
 * Se lanza cuando el usuario autenticado intenta operar sobre algo
 * que su rol no le permite gestionar. Por ejemplo:
 * - un GERENTE que intenta crear, modificar o dar de baja a otro GERENTE;
 * - un SYS_ADMIN que intenta gestionar vendedores o cocineros.
 * Se responde con HTTP 403 (Forbidden).
 */
public class OperacionNoPermitidaException extends NegocioException {

    public OperacionNoPermitidaException(String mensaje) {
        super(mensaje);
    }
}