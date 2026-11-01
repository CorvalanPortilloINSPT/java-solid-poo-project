package com.mycompany.frontend_fastfood.controller;

import com.mycompany.frontend_fastfood.cliente.BackendClient;
import com.mycompany.frontend_fastfood.cliente.BackendException;
import com.mycompany.frontend_fastfood.cliente.Credenciales;
import com.mycompany.frontend_fastfood.cliente.Sesion;
import com.mycompany.frontend_fastfood.dto.EmpleadoDTO;
import com.mycompany.frontend_fastfood.dto.IngredienteDTO;
import com.mycompany.frontend_fastfood.dto.PedidoDTO;
import com.mycompany.frontend_fastfood.dto.PedidoDetalleDTO;
import com.mycompany.frontend_fastfood.dto.PedidoRequestDTO;
import com.mycompany.frontend_fastfood.dto.ProductoDTO;
import com.mycompany.frontend_fastfood.dto.ProductoIngredienteDTO;
import com.mycompany.frontend_fastfood.dto.ReporteVentasDTO;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Pantallas de pedidos.
 *
 * Reemplaza a:
 * - VendedorControlador: "Crear pedido" y "Consultar pedidos".
 * - CocineroControlador: "Ver pedidos pendientes" y "Cambiar estado del pedido".
 * - GerenteControlador: "Gestionar pedidos" (listar y eliminar pendientes)
 *   y "Reporte de ventas".
 */
@Controller
public class PedidoController {

    /** Atributo de sesión donde vive el pedido en armado del vendedor. */
    private static final String PEDIDO_EN_ARMADO = "pedidoEnArmado";

    private final BackendClient backendClient;

    public PedidoController(BackendClient backendClient) {
        this.backendClient = backendClient;
    }

    // ------------------------------------------------------------------
    // Listado y detalle (vendedor y gerente)
    // ------------------------------------------------------------------

    /** Todos los pedidos, con su total. */
    @GetMapping("/pedidos")
    public String listar(HttpSession sesion, Model model) {

        List<PedidoDTO> pedidos = backendClient.getLista(
                "/api/pedidos", PedidoDTO[].class, Sesion.credenciales(sesion));

        model.addAttribute("pedidos", pedidos);
        model.addAttribute("puedeEliminar", Sesion.tieneRol(sesion, "GERENTE"));

        return "pedidosListado";
    }

    /** Detalle de un pedido: ítems, modificaciones y totales. */
    @GetMapping("/pedidos/{id}")
    public String detalle(@PathVariable String id, HttpSession sesion, Model model) {

        PedidoDetalleDTO pedido = backendClient.get(
                "/api/pedidos/" + id, PedidoDetalleDTO.class, Sesion.credenciales(sesion));

        model.addAttribute("pedido", pedido);
        model.addAttribute("puedeEliminar", Sesion.tieneRol(sesion, "GERENTE"));

        return "pedidoDetalle";
    }

    /** Elimina un pedido pendiente (solo gerente). */
    @PostMapping("/pedidos/{id}/eliminar")
    public String eliminar(
            @PathVariable String id,
            HttpSession sesion,
            RedirectAttributes redirect) {

        try {
            backendClient.delete("/api/pedidos/" + id, Sesion.credenciales(sesion));
            redirect.addFlashAttribute("exito", "Pedido " + id + " eliminado.");

        } catch (BackendException e) {
            if (e.isNoAutenticado()) {
                throw e;
            }
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/pedidos";
    }

    // ------------------------------------------------------------------
    // Pendientes y despacho (cocinero)
    // ------------------------------------------------------------------

    /** Pendientes con su detalle (ítems y modificaciones). */
    @GetMapping("/pedidos/pendientes")
    public String pendientes(HttpSession sesion, Model model) {

        Credenciales credenciales = Sesion.credenciales(sesion);

        List<PedidoDetalleDTO> pedidos = backendClient.getLista(
                "/api/pedidos/pendientes", PedidoDetalleDTO[].class, credenciales);

        model.addAttribute("pedidos", pedidos);
        model.addAttribute("puedeDespachar", Sesion.tieneRol(sesion, "COCINERO"));

        return "pendientes";
    }

    /** Despacha un pedido; el cocinero es el usuario logueado. */
    @PostMapping("/pedidos/{id}/despachar")
    public String despachar(
            @PathVariable String id,
            HttpSession sesion,
            RedirectAttributes redirect) {

        try {
            backendClient.put(
                    "/api/pedidos/" + id + "/despacho",
                    null,
                    PedidoDetalleDTO.class,
                    Sesion.credenciales(sesion));

            redirect.addFlashAttribute("exito", "Pedido " + id + " despachado.");

        } catch (BackendException e) {
            if (e.isNoAutenticado()) {
                throw e;
            }
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/pedidos/pendientes";
    }

    // ------------------------------------------------------------------
    // Registrar pedido (vendedor)
    // ------------------------------------------------------------------

    /** Pantalla del pedido en armado. */
    @GetMapping("/pedidos/nuevo")
    public String nuevo(HttpSession sesion, Model model) {

        verificarVendedor(sesion);
        Credenciales credenciales = Sesion.credenciales(sesion);

        model.addAttribute("pedidoEnArmado", pedidoEnArmado(sesion));
        model.addAttribute("productos", backendClient.getLista(
                "/api/productos/disponibles", ProductoDTO[].class, credenciales));
        model.addAttribute("ingredientes", backendClient.getLista(
                "/api/ingredientes", IngredienteDTO[].class, credenciales));

        return "pedidoNuevo";
    }

    /** Agrega un producto al pedido en armado. */
    @PostMapping("/pedidos/nuevo/items")
    public String agregarItem(
            @RequestParam String idProducto,
            @RequestParam(required = false) Integer cantidad,
            HttpSession sesion,
            RedirectAttributes redirect) {

        verificarVendedor(sesion);

        if (cantidad == null || cantidad <= 0) {
            redirect.addFlashAttribute("error", "La cantidad debe ser mayor a cero.");
            return "redirect:/pedidos/nuevo";
        }

        try {
            Credenciales credenciales = Sesion.credenciales(sesion);

            ProductoDTO producto = backendClient.get(
                    "/api/productos/" + idProducto, ProductoDTO.class, credenciales);

            List<ProductoIngredienteDTO> receta = backendClient.getLista(
                    "/api/productos/" + idProducto + "/ingredientes",
                    ProductoIngredienteDTO[].class, credenciales);

            PedidoRequestDTO.ItemRequest item = new PedidoRequestDTO.ItemRequest();
            item.setIdProducto(producto.getId());
            item.setCantidad(cantidad);
            item.setDescripcionProducto(producto.getDescripcion());
            item.setPrecio(producto.getPrecio());
            item.setReceta(receta);

            pedidoEnArmado(sesion).getItems().add(item);

        } catch (BackendException e) {
            if (e.isNoAutenticado()) {
                throw e;
            }
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/pedidos/nuevo";
    }

    /** Quita un ítem del pedido en armado. */
    @PostMapping("/pedidos/nuevo/items/{numero}/quitar")
    public String quitarItem(
            @PathVariable int numero,
            HttpSession sesion,
            RedirectAttributes redirect) {

        verificarVendedor(sesion);
        PedidoRequestDTO pedido = pedidoEnArmado(sesion);

        if (numeroValido(numero, pedido.getItems().size(), "ítem", redirect)) {
            pedido.quitarItem(numero);
        }

        return "redirect:/pedidos/nuevo";
    }

    /** Agrega una modificación (AGREGADO / QUITADO) a un ítem. */
    @PostMapping("/pedidos/nuevo/items/{numero}/modificaciones")
    public String agregarModificacion(
            @PathVariable int numero,
            @RequestParam String idIngrediente,
            @RequestParam String tipo,
            @RequestParam(required = false) Integer cantidad,
            HttpSession sesion,
            RedirectAttributes redirect) {

        verificarVendedor(sesion);
        PedidoRequestDTO pedido = pedidoEnArmado(sesion);

        if (!numeroValido(numero, pedido.getItems().size(), "ítem", redirect)) {
            return "redirect:/pedidos/nuevo";
        }

        if (cantidad == null || cantidad <= 0) {
            redirect.addFlashAttribute("error", "La cantidad debe ser mayor a cero.");
            return "redirect:/pedidos/nuevo";
        }

        if (!"AGREGADO".equals(tipo) && !"QUITADO".equals(tipo)) {
            redirect.addFlashAttribute("error", "Tipo de modificación inválido.");
            return "redirect:/pedidos/nuevo";
        }

        try {
            IngredienteDTO ingrediente = backendClient.get(
                    "/api/ingredientes/" + idIngrediente,
                    IngredienteDTO.class,
                    Sesion.credenciales(sesion));

            PedidoRequestDTO.ModificacionRequest modificacion =
                    new PedidoRequestDTO.ModificacionRequest();
            modificacion.setIdIngrediente(ingrediente.getId());
            modificacion.setTipo(tipo);
            modificacion.setCantidad(cantidad);
            modificacion.setDescripcionIngrediente(ingrediente.getDescripcion());
            modificacion.setPrecioUnitario(ingrediente.getPrecio());

            pedido.item(numero).getModificaciones().add(modificacion);

        } catch (BackendException e) {
            if (e.isNoAutenticado()) {
                throw e;
            }
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/pedidos/nuevo";
    }

    /** Quita una modificación de un ítem. */
    @PostMapping("/pedidos/nuevo/items/{numero}/modificaciones/{numeroMod}/quitar")
    public String quitarModificacion(
            @PathVariable int numero,
            @PathVariable int numeroMod,
            HttpSession sesion,
            RedirectAttributes redirect) {

        verificarVendedor(sesion);
        PedidoRequestDTO pedido = pedidoEnArmado(sesion);

        if (numeroValido(numero, pedido.getItems().size(), "ítem", redirect)) {
            PedidoRequestDTO.ItemRequest item = pedido.item(numero);

            if (numeroValido(numeroMod, item.getModificaciones().size(), "modificación", redirect)) {
                item.quitarModificacion(numeroMod);
            }
        }

        return "redirect:/pedidos/nuevo";
    }

    /** Descarta el pedido en armado. */
    @PostMapping("/pedidos/nuevo/cancelar")
    public String cancelar(HttpSession sesion, RedirectAttributes redirect) {

        verificarVendedor(sesion);
        sesion.removeAttribute(PEDIDO_EN_ARMADO);
        redirect.addFlashAttribute("exito", "Pedido descartado.");

        return "redirect:/pedidos/nuevo";
    }

    /** Envía el pedido completo al backend en un solo POST. */
    @PostMapping("/pedidos/nuevo/confirmar")
    public String confirmar(HttpSession sesion, RedirectAttributes redirect) {

        verificarVendedor(sesion);
        PedidoRequestDTO pedido = pedidoEnArmado(sesion);

        if (pedido.isVacio()) {
            redirect.addFlashAttribute("error", "El pedido debe tener al menos un producto.");
            return "redirect:/pedidos/nuevo";
        }

        try {
            PedidoDetalleDTO creado = backendClient.post(
                    "/api/pedidos", pedido, PedidoDetalleDTO.class,
                    Sesion.credenciales(sesion));

            sesion.removeAttribute(PEDIDO_EN_ARMADO);
            redirect.addFlashAttribute("exito", "Pedido " + creado.getId() + " registrado.");

            return "redirect:/pedidos/" + creado.getId();

        } catch (BackendException e) {
            if (e.isNoAutenticado()) {
                throw e;
            }
            // El pedido en armado queda como estaba para poder corregirlo.
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/pedidos/nuevo";
        }
    }

    // ------------------------------------------------------------------
    // Reporte de ventas (gerente)
    // ------------------------------------------------------------------

    /**
     * Pedidos DESPACHADOS, con filtros opcionales de fechas y vendedor.
     * Es un GET: los filtros quedan en la URL (se puede recargar o guardar).
     */
    @GetMapping("/pedidos/reporte")
    public String reporte(
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta,
            @RequestParam(required = false) String idVendedor,
            HttpSession sesion,
            Model model) {

        verificarGerente(sesion);
        Credenciales credenciales = Sesion.credenciales(sesion);

        // Empleados para el desplegable y para mostrar nombres en la tabla
        // (el reporte trae solo los IDs de vendedor y cocinero).
        List<EmpleadoDTO> empleados = backendClient.getLista(
                "/api/empleados", EmpleadoDTO[].class, credenciales);

        Map<String, String> nombres = empleados.stream()
                .collect(Collectors.toMap(
                        EmpleadoDTO::getId,
                        e -> e.getApellido() + ", " + e.getNombre()));

        // Solo se mandan los filtros que se completaron.
        String ruta = UriComponentsBuilder.fromPath("/api/pedidos/reporte")
                .queryParamIfPresent("desde", textoOpcional(desde))
                .queryParamIfPresent("hasta", textoOpcional(hasta))
                .queryParamIfPresent("idVendedor", textoOpcional(idVendedor))
                .build()
                .toUriString();

        try {
            ReporteVentasDTO reporte = backendClient.get(
                    ruta, ReporteVentasDTO.class, credenciales);

            model.addAttribute("reporte", reporte);

        } catch (BackendException e) {
            if (e.isNoAutenticado()) {
                throw e;
            }
            // Se muestra el error con los filtros cargados, para corregirlos.
            model.addAttribute("error", e.getMessage());
        }

        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("idVendedor", idVendedor);
        model.addAttribute("empleados", empleados);
        model.addAttribute("nombresEmpleados", nombres);

        return "pedidosReporte";
    }

    // ------------------------------------------------------------------
    // Auxiliares
    // ------------------------------------------------------------------

    /** Devuelve el pedido en armado de la sesión, o crea uno vacío. */
    private PedidoRequestDTO pedidoEnArmado(HttpSession sesion) {

        PedidoRequestDTO pedido = (PedidoRequestDTO) sesion.getAttribute(PEDIDO_EN_ARMADO);

        if (pedido == null) {
            pedido = new PedidoRequestDTO();
            sesion.setAttribute(PEDIDO_EN_ARMADO, pedido);
        }

        return pedido;
    }

    /** Solo un vendedor registra pedidos (igual que en el backend). */
    private void verificarVendedor(HttpSession sesion) {
        if (!Sesion.tieneRol(sesion, "VENDEDOR")) {
            throw new BackendException(403, "Solo un vendedor puede registrar pedidos.");
        }
    }

    /** Solo el gerente ve el reporte de ventas (igual que en el backend). */
    private void verificarGerente(HttpSession sesion) {
        if (!Sesion.tieneRol(sesion, "GERENTE")) {
            throw new BackendException(403, "Solo el gerente puede ver el reporte de ventas.");
        }
    }

    /** Verifica que el número de ítem o modificación exista (empiezan en 1). */
    private boolean numeroValido(
            int numero, int cantidad, String que, RedirectAttributes redirect) {

        if (numero < 1 || numero > cantidad) {
            redirect.addFlashAttribute("error", "No existe ese " + que + ".");
            return false;
        }

        return true;
    }

    /** Texto vacío -> sin filtro. */
    private Optional<String> textoOpcional(String texto) {
        return (texto == null || texto.isBlank())
                ? Optional.empty()
                : Optional.of(texto.trim());
    }
}