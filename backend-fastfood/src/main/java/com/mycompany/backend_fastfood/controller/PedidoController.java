package com.mycompany.backend_fastfood.controller;

import com.mycompany.backend_fastfood.model.Pedido;
import com.mycompany.backend_fastfood.model.ReporteVentas;
import com.mycompany.backend_fastfood.service.PedidoService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de pedidos. Toda la lógica está en PedidoService.
 *
 * Recibe y devuelve la entidad Pedido directamente (sin DTO): cada
 * pedido sale con sus ítems, sus modificaciones, los subtotales y el
 * total, que calcula el backend (ver Pedido, Item e ItemIngrediente).
 *
 * - VENDEDOR: registra pedidos y consulta.
 * - COCINERO: consulta pendientes y despacha.
 * - GERENTE: consulta, elimina pendientes y obtiene el reporte de ventas.
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // ------------------------------------------------------------------
    // Consultas
    // ------------------------------------------------------------------

    /** Todos los pedidos, con su total. */
    @GetMapping
    public List<Pedido> listarPedidos() {
        return pedidoService.listarTodos();
    }

    /** Pendientes con su detalle completo: lo que ve el cocinero. */
    @GetMapping("/pendientes")
    public List<Pedido> listarPendientes() {
        return pedidoService.listarPendientes();
    }

    /**
     * Reporte de ventas (pedidos DESPACHADOS).
     * Filtros opcionales, por ejemplo:
     * /api/pedidos/reporte?desde=2026-10-01&hasta=2026-10-31&idVendedor=E0002
     */
    @GetMapping("/reporte")
    public ReporteVentas reporteVentas(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) String idVendedor) {

        return pedidoService.generarReporte(desde, hasta, idVendedor);
    }

    /** Detalle completo de un pedido. */
    @GetMapping("/{id}")
    public Pedido buscarPedido(@PathVariable String id) {
        return pedidoService.buscar(id);
    }

    // ------------------------------------------------------------------
    // Operaciones
    // ------------------------------------------------------------------

    /**
     * Registra un pedido. El vendedor es el usuario autenticado.
     * Body:
     * {
     *   "items": [
     *     { "idProducto": "PR001", "cantidad": 3 },
     *     { "idProducto": "PR001", "cantidad": 1,
     *       "modificaciones": [
     *         { "idIngrediente": "ING02", "tipo": "AGREGADO", "cantidad": 1 },
     *         { "idIngrediente": "ING04", "tipo": "QUITADO",  "cantidad": 1 }
     *       ] }
     *   ]
     * }
     * La cantidad de cada modificación es POR UNIDAD del ítem.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pedido registrarPedido(
            @RequestBody Pedido datos,
            Authentication auth) {

        return pedidoService.registrar(auth.getName(), datos);
    }

    /** El cocinero es el usuario autenticado. */
    @PutMapping("/{id}/despacho")
    public Pedido despacharPedido(
            @PathVariable String id,
            Authentication auth) {

        return pedidoService.despachar(auth.getName(), id);
    }

    /** Solo pedidos pendientes. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarPedido(@PathVariable String id) {
        pedidoService.eliminar(id);
    }
}
