package com.mycompany.frontend_fastfood.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Detalle completo de un pedido, tal como lo devuelve el backend
 * (PedidoDetalleDTO): pedido -> ítems -> modificaciones.
 *
 * Jackson arma los tres niveles automáticamente a partir del JSON.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PedidoDetalleDTO {

    private String id;
    private LocalDate fecha;
    private String estado;
    private String idVendedor;
    private String nombreVendedor;
    private String idCocinero;
    private String nombreCocinero;
    private List<ItemDetalle> items = new ArrayList<>();
    private double total;

    /** Un producto del pedido. */
    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItemDetalle {

        private String id;
        private String idProducto;
        private String descripcionProducto;
        private int cantidad;
        private double precioVenta;
        private List<ModificacionDetalle> modificaciones = new ArrayList<>();
        private double subtotal;
    }

    /** Un ingrediente agregado o quitado. */
    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ModificacionDetalle {

        private String idIngrediente;
        private String descripcionIngrediente;
        private String tipo;
        private int cantidadPorUnidad;
        private int cantidadTotal;
        private double precioUnitario;
    }
}