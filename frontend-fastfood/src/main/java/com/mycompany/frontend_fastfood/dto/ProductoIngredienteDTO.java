package com.mycompany.frontend_fastfood.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Una línea de la receta de un producto, tal como la devuelve
 * GET /api/productos/{id}/ingredientes (ProductoIngredienteDTO del backend).
 *
 * Serializable: se guarda en la sesión dentro del pedido en armado,
 * para mostrar la receta de cada ítem sin volver a pedirla.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductoIngredienteDTO implements Serializable {

    private String idProducto;
    private String descripcionProducto;
    private String idIngrediente;
    private String descripcionIngrediente;
    private boolean obligatorio;
}