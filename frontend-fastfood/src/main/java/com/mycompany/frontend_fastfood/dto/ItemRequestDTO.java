package com.mycompany.frontend_fastfood.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Un producto del pedido en armado, con su cantidad y sus
 * modificaciones. Lo usa PedidoRequestDTO.
 *
 * Se envían al backend: idProducto, cantidad y modificaciones.
 * Los campos con @JsonIgnore son solo para mostrar en pantalla.
 */
@Getter
@Setter
@NoArgsConstructor
public class ItemRequestDTO implements Serializable {

    // --- Se envían al backend ---
    private String idProducto;
    private Integer cantidad;
    private List<ModificacionRequestDTO> modificaciones = new ArrayList<>();

    // --- Solo para mostrar ---
    @JsonIgnore
    private String descripcionProducto;

    @JsonIgnore
    private double precio;

    @JsonIgnore
    private List<ProductoIngredienteDTO> receta = new ArrayList<>();

    /**
     * Estimación para mostrar en pantalla:
     * cantidad × (precio + agregados por unidad).
     * El subtotal real lo calcula el backend al registrar el pedido.
     */
    @JsonIgnore
    public double getSubtotalEstimado() {

        double agregadosPorUnidad = 0;

        // Solo los agregados suman; los quitados no descuentan.
        for (ModificacionRequestDTO modificacion : modificaciones) {
            if ("AGREGADO".equals(modificacion.getTipo())) {
                agregadosPorUnidad += modificacion.getPrecioUnitario()
                        * modificacion.getCantidad();
            }
        }

        return cantidad * (precio + agregadosPorUnidad);
    }

    /** Quita una modificación por número de pantalla (empieza en 1). */
    public void quitarModificacion(int numero) {
        modificaciones.remove(numero - 1);
    }
}