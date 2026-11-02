package com.mycompany.backend_fastfood.exception;

/**
 * Se lanza cuando la operación viola una regla de negocio:
 * descripción/DNI/usuario duplicado, quitar un ingrediente obligatorio,
 * dar de baja algo que está en pedidos pendientes, despachar un pedido
 * que ya fue despachado, etc.
 * Se responde con HTTP 409 (Conflict).
 *
 * Reemplaza a DniDuplicadoExcepcion, IngredienteDuplicadoExcepcion
 * y ProductoDuplicadoExcepcion del sistema anterior.
 */
public class ReglaNegocioException extends NegocioException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}