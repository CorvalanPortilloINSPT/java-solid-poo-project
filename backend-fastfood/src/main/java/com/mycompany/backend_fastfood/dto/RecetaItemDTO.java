package com.mycompany.backend_fastfood.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Datos para agregar un ingrediente a la receta de un producto.
 *
 * Ejemplo de body:
 * {
 *   "idIngrediente": "ING02",
 *   "obligatorio": false
 * }
 *
 * Si no se envía "obligatorio", se toma como false (opcional).
 */
@Getter
@Setter
@NoArgsConstructor
public class RecetaItemDTO {

    private String idIngrediente;
    private boolean obligatorio;
}