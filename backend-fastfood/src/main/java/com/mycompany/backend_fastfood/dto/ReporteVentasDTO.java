package com.mycompany.backend_fastfood.dto;

import com.mycompany.backend_fastfood.model.Pedido;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Reporte de ventas del gerente: pedidos DESPACHADOS, con filtros
 * opcionales por fechas y vendedor.
 *
 * Ejemplo:
 * {
 *   "desde": "2026-10-01",
 *   "hasta": "2026-10-31",
 *   "idVendedor": null,
 *   "cantidadVentas": 9,
 *   "totalVentas": 52300.0,
 *   "pedidos": [ { "id": "PE002", "fecha": "2026-10-05", "total": 4500.0, ... } ]
 * }
 *
 * Los filtros que no se usaron vuelven en null.
 */
@Getter
@AllArgsConstructor
public class ReporteVentasDTO {

    private LocalDate desde;
    private LocalDate hasta;
    private String idVendedor;
    private int cantidadVentas;
    private double totalVentas;
    private List<PedidoDTO> pedidos;

    public static ReporteVentasDTO desde(
            List<Pedido> pedidos,
            LocalDate desde,
            LocalDate hasta,
            String idVendedor) {

        List<PedidoDTO> resumen = pedidos.stream()
                .map(PedidoDTO::desde)
                .toList();

        double total = resumen.stream()
                .mapToDouble(PedidoDTO::getTotal)
                .sum();

        return new ReporteVentasDTO(
                desde,
                hasta,
                idVendedor,
                resumen.size(),
                total,
                resumen
        );
    }
}