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
 * Vive en la sesión del front mientras el vendedor agrega ítems y
 * modificaciones. Al confirmar, este mismo objeto se manda como body
 * de POST /api/pedidos (PedidoRequestDTO del backend).
 *
 * Los campos marcados con @JsonIgnore son solo para mostrar en pantalla
 * (descripciones, precios, receta) y no se envían al backend.
 * El total que se muestra es una estimación: el total real lo calcula
 * el backend al registrar el pedido.
 */
@Getter
@Setter
@NoArgsConstructor
public class PedidoRequestDTO implements Serializable {

    private List<ItemRequest> items = new ArrayList<>();

    @JsonIgnore
    public boolean isVacio() {
        return items.isEmpty();
    }

    @JsonIgnore
    public double getTotalEstimado() {
        return items.stream()
                .mapToDouble(ItemRequest::getSubtotalEstimado)
                .sum();
    }

    /** Ítem por número de pantalla (empieza en 1, igual que "Ítem N" del backend). */
    public ItemRequest item(int numero) {
        return items.get(numero - 1);
    }

    public void quitarItem(int numero) {
        items.remove(numero - 1);
    }

    // ------------------------------------------------------------------

    /** Un producto del pedido, con su cantidad y modificaciones. */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ItemRequest implements Serializable {

        // --- Se envían al backend ---
        private String idProducto;
        private Integer cantidad;
        private List<ModificacionRequest> modificaciones = new ArrayList<>();

        // --- Solo para mostrar ---
        @JsonIgnore
        private String descripcionProducto;

        @JsonIgnore
        private double precio;

        @JsonIgnore
        private List<ProductoIngredienteDTO> receta = new ArrayList<>();

        /** Estimación: cantidad × (precio + agregados por unidad). */
        @JsonIgnore
        public double getSubtotalEstimado() {

            double agregadosPorUnidad = modificaciones.stream()
                    .filter(m -> "AGREGADO".equals(m.getTipo()))
                    .mapToDouble(m -> m.getPrecioUnitario() * m.getCantidad())
                    .sum();

            return cantidad * (precio + agregadosPorUnidad);
        }

        public void quitarModificacion(int numero) {
            modificaciones.remove(numero - 1);
        }
    }

    /** Un ingrediente agregado o quitado, por unidad del ítem. */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ModificacionRequest implements Serializable {

        // --- Se envían al backend ---
        private String idIngrediente;
        private String tipo;
        private Integer cantidad;

        // --- Solo para mostrar ---
        @JsonIgnore
        private String descripcionIngrediente;

        @JsonIgnore
        private double precioUnitario;
    }
}