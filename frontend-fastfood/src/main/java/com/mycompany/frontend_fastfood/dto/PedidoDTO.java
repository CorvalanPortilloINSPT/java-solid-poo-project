package com.mycompany.frontend_fastfood.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Resumen de un pedido para el listado y el reporte de ventas
 * (GET /api/pedidos y GET /api/pedidos/reporte).
 *
 * El backend devuelve la entidad Pedido completa; acá se toman solo los
 * datos que muestran esas pantallas (ignoreUnknown descarta el resto,
 * por ejemplo los ítems). El nombre del vendedor y del cocinero ya
 * viene calculado por el backend. Para ítems y modificaciones, ver
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
    private String idVendedor;
    private String nombreVendedor;
    private String idCocinero;
    private String nombreCocinero;
    private double total;

    /** Para el JSP: ${pedido.pendiente}. */
    public boolean isPendiente() {
        return "PENDIENTE".equals(estado);
    }
}
