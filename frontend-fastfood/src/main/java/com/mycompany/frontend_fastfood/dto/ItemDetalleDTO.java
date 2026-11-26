package com.mycompany.frontend_fastfood.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Un producto de un pedido ya registrado, tal como lo devuelve el
 * backend (la entidad Item): precio congelado, modificaciones y
 * subtotal calculado por el backend. Lo usa PedidoDetalleDTO.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemDetalleDTO {

    private String id;
    private String idProducto;
    private String descripcionProducto;
    private int cantidad;
    private double precioVenta;
    private List<ModificacionDetalleDTO> modificaciones = new ArrayList<>();
    private double subtotal;
}