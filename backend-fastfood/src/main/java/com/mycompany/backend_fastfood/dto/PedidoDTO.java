package com.mycompany.backend_fastfood.dto;

import com.mycompany.backend_fastfood.model.Pedido;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Resumen de un pedido: se usa en los listados y en el reporte de ventas.
 * Para ver los ítems y modificaciones, usar PedidoDetalleDTO.
 */
@Getter
@AllArgsConstructor
public class PedidoDTO {

    private String id;
    private LocalDate fecha;
    private String estado;
    private String empleadoVendedor;
    private String empleadoCocinero;
    private double total;

    public static PedidoDTO desde(Pedido pedido) {
        return new PedidoDTO(
                pedido.getID(),
                pedido.getFecha(),
                pedido.getEstado() != null
                        ? pedido.getEstado().name()
                        : null,
                pedido.getEmpleadoVendedor() != null
                        ? pedido.getEmpleadoVendedor().getID()
                        : null,
                pedido.getEmpleadoCocinero() != null
                        ? pedido.getEmpleadoCocinero().getID()
                        : null,
                pedido.calcularTotal()
        );
    }
}