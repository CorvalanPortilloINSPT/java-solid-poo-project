package com.mycompany.frontend_fastfood.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Pedido en armado (el "carrito" del vendedor).
 *
 * Vive en la sesión del front mientras el vendedor agrega ítems
 * (ItemRequestDTO) y modificaciones (ModificacionRequestDTO). Al
 * confirmar, este mismo objeto se manda como body de POST /api/pedidos:
 * el backend lo recibe como entidad Pedido.
 *
 * El total que se muestra es una estimación: el total real lo calcula
 * el backend al registrar el pedido.
 */
@Getter
@Setter
@NoArgsConstructor
public class PedidoRequestDTO implements Serializable {

    private List<ItemRequestDTO> items = new ArrayList<>();

    @JsonIgnore
    public boolean isVacio() {
        return items.isEmpty();
    }

    @JsonIgnore
    public double getTotalEstimado() {

        double total = 0;

        for (ItemRequestDTO item : items) {
            total += item.getSubtotalEstimado();
        }

        return total;
    }

    /** Ítem por número de pantalla (empieza en 1, igual que "Ítem N" del backend). */
    public ItemRequestDTO item(int numero) {
        return items.get(numero - 1);
    }

    public void quitarItem(int numero) {
        items.remove(numero - 1);
    }
}