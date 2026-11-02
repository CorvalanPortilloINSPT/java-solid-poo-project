package com.mycompany.backend_fastfood.exception;

/**
 * Se lanza cuando se busca un registro por ID y no existe.
 * Se responde con HTTP 404.
 *
 * Reemplaza a PedidoNoEncontradoExcepcion y RegistroNoEncontradoExcepcion
 * del sistema anterior.
 */
public class RecursoNoEncontradoException extends NegocioException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    /**
     * Arma el mensaje estándar. Ejemplo:
     * new RecursoNoEncontradoException("ingrediente", "ING07")
     * → "No existe un ingrediente con ID: ING07"
     */
    public RecursoNoEncontradoException(String entidad, String id) {
        super("No existe un " + entidad + " con ID: " + id);
    }
}