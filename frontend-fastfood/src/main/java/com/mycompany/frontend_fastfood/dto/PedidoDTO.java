package com.mycompany.frontend_fastfood.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Resumen de un pedido, tal como lo devuelve GET /api/pedidos
 * (PedidoDTO del backend). Para ítems y modificaciones, ver
 * PedidoDetalleDTO.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PedidoDTO {

    private String id;
    private LocalDate fecha;
    private String estado;
    private String empleadoVendedor;
    private String empleadoCocinero;
    private double total;

    /** Para el JSP: ${pedido.pendiente}. */
    public boolean isPendiente() {
        return "PENDIENTE".equals(estado);
    }
}