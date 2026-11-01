package com.mycompany.frontend_fastfood.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Reporte de ventas tal como lo devuelve el backend
 * (GET /api/pedidos/reporte).
 *
 * Mismos campos que ReporteVentasDTO del backend:
 * {
 *   "desde": "2026-10-01",
 *   "hasta": "2026-10-31",
 *   "idVendedor": null,
 *   "cantidadVentas": 9,
 *   "totalVentas": 52300.0,
 *   "pedidos": [ { "id": "PE002", "fecha": "2026-10-05", ... } ]
 * }
 *
 * Las fechas se reciben como texto (AAAA-MM-DD): el front solo las
 * muestra y las vuelve a poner en el formulario de filtros.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReporteVentasDTO implements Serializable {

    private String desde;
    private String hasta;
    private String idVendedor;
    private int cantidadVentas;
    private double totalVentas;
    private List<PedidoDTO> pedidos = new ArrayList<>();
}