package com.mycompany.backend_fastfood.controller;

import com.mycompany.backend_fastfood.dto.PedidoDTO;
import com.mycompany.backend_fastfood.dto.PedidoDetalleDTO;
import com.mycompany.backend_fastfood.dto.PedidoRequestDTO;
import com.mycompany.backend_fastfood.dto.ReporteVentasDTO;
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

    /** Listado resumido de todos los pedidos, con su total. */
    @GetMapping
    public List<PedidoDTO> listarPedidos() {
        return pedidoService.listarTodos()
                .stream()
                .map(PedidoDTO::desde)
                .toList();
    }

    /** Pendientes con su detalle completo: lo que ve el cocinero. */
    @GetMapping("/pendientes")
    public List<PedidoDetalleDTO> listarPendientes() {
        return pedidoService.listarPendientes()
                .stream()
                .map(PedidoDetalleDTO::desde)
                .toList();
    }

    /**
     * Reporte de ventas (pedidos DESPACHADOS).
     * Filtros opcionales, por ejemplo:
     * /api/pedidos/reporte?desde=2026-10-01&hasta=2026-10-31&idVendedor=E0002
     */
    @GetMapping("/reporte")
    public ReporteVentasDTO reporteVentas(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,

            @RequestParam(required = false) String idVendedor) {

        return ReporteVentasDTO.desde(
                pedidoService.pedidosParaReporte(desde, hasta, idVendedor),
                desde,
                hasta,
                idVendedor
        );
    }

    /** Detalle completo de un pedido. */
    @GetMapping("/{id}")
    public PedidoDetalleDTO buscarPedido(@PathVariable String id) {
        return PedidoDetalleDTO.desde(pedidoService.buscar(id));
    }

    // ------------------------------------------------------------------
    // Operaciones
    // ------------------------------------------------------------------

    /** El vendedor es el usuario autenticado. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoDetalleDTO registrarPedido(
            @RequestBody PedidoRequestDTO dto,
            Authentication auth) {

        return PedidoDetalleDTO.desde(
                pedidoService.registrar(auth.getName(), dto));
    }

    /** El cocinero es el usuario autenticado. */
    @PutMapping("/{id}/despacho")
    public PedidoDetalleDTO despacharPedido(
            @PathVariable String id,
            Authentication auth) {

        return PedidoDetalleDTO.desde(
                pedidoService.despachar(auth.getName(), id));
    }

    /** Solo pedidos pendientes. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarPedido(@PathVariable String id) {
        pedidoService.eliminar(id);
    }
}