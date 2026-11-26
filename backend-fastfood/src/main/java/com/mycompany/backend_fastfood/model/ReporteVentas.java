package com.mycompany.backend_fastfood.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Reporte de ventas del gerente: pedidos DESPACHADOS, con filtros
 * opcionales por fechas y vendedor.
 *
 * No es una entidad (no tiene @Entity ni tabla): es un resultado que
 * calcula el backend a partir de los pedidos. Así el total y la
 * cantidad de ventas se ven igual desde el front y desde Postman.
 *
 * JSON:
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
 *
 * Es inmutable: atributos final, sin setters, y la lista de pedidos
 * se copia al crear el reporte y al devolverla (copia defensiva).
 */
public class ReporteVentas {

    private final LocalDate desde;
    private final LocalDate hasta;
    private final String idVendedor;
    private final List<Pedido> pedidos;
    private final int cantidadVentas;
    private final double totalVentas;

    public ReporteVentas(
            LocalDate desde,
            LocalDate hasta,
            String idVendedor,
            List<Pedido> pedidos) {

        this.desde = desde;
        this.hasta = hasta;
        this.idVendedor = idVendedor;
        this.pedidos = new ArrayList<>(pedidos);
        this.cantidadVentas = pedidos.size();

        double total = 0;
        for (Pedido pedido : pedidos) {
            total += pedido.calcularTotal();
        }
        this.totalVentas = total;
    }

    public LocalDate getDesde() {
        return desde;
    }

    public LocalDate getHasta() {
        return hasta;
    }

    public String getIdVendedor() {
        return idVendedor;
    }

    public List<Pedido> getPedidos() {
        return new ArrayList<>(pedidos);
    }

    public int getCantidadVentas() {
        return cantidadVentas;
    }

    public double getTotalVentas() {
        return totalVentas;
    }
}