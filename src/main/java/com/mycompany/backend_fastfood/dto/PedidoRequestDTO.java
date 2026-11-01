package com.mycompany.backend_fastfood.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Datos que envía el vendedor para registrar un pedido.
 *
 * El ID, la fecha, el estado y el vendedor los define el sistema
 * (el vendedor es el usuario autenticado).
 *
 * Ejemplo:
 * {
 *   "items": [
 *     {
 *       "idProducto": "PR001",
 *       "cantidad": 3
 *     },
 *     {
 *       "idProducto": "PR001",
 *       "cantidad": 1,
 *       "modificaciones": [
 *         { "idIngrediente": "ING02", "tipo": "AGREGADO", "cantidad": 1 },
 *         { "idIngrediente": "ING04", "tipo": "QUITADO",  "cantidad": 1 }
 *       ]
 *     }
 *   ]
 * }
 *
 * La cantidad de cada modificación es POR UNIDAD del ítem.
 */
@Getter
@Setter
@NoArgsConstructor
public class PedidoRequestDTO {

    private List<ItemRequest> items = new ArrayList<>();

    /** Un producto del pedido, con su cantidad y modificaciones. */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ItemRequest {

        private String idProducto;

        /** Integer (no int) para detectar si no se envió. */
        private Integer cantidad;

        /** Opcional: si no se envía, el producto va con su receta original. */
        private List<ModificacionRequest> modificaciones = new ArrayList<>();
    }

    /** Un ingrediente agregado o quitado, por unidad del ítem. */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ModificacionRequest {

        private String idIngrediente;

        /** "AGREGADO" o "QUITADO". Texto para dar un mensaje claro si es inválido. */
        private String tipo;

        /** Integer (no int) para detectar si no se envió. */
        private Integer cantidad;
    }
}