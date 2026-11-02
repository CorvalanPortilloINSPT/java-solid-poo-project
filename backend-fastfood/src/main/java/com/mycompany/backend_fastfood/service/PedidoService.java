package com.mycompany.backend_fastfood.service;

import com.mycompany.backend_fastfood.dto.PedidoRequestDTO;
import com.mycompany.backend_fastfood.exception.DatoInvalidoException;
import com.mycompany.backend_fastfood.exception.OperacionNoPermitidaException;
import com.mycompany.backend_fastfood.exception.RecursoNoEncontradoException;
import com.mycompany.backend_fastfood.exception.ReglaNegocioException;
import com.mycompany.backend_fastfood.model.Empleado;
import com.mycompany.backend_fastfood.model.EstadoPedido;
import com.mycompany.backend_fastfood.model.Ingrediente;
import com.mycompany.backend_fastfood.model.Item;
import com.mycompany.backend_fastfood.model.ItemIngrediente;
import com.mycompany.backend_fastfood.model.Pedido;
import com.mycompany.backend_fastfood.model.Producto;
import com.mycompany.backend_fastfood.model.ProductoIngrediente;
import com.mycompany.backend_fastfood.model.ProductoIngredienteId;
import com.mycompany.backend_fastfood.model.Rol;
import com.mycompany.backend_fastfood.model.TipoModificacion;
import com.mycompany.backend_fastfood.repository.EmpleadoRepository;
import com.mycompany.backend_fastfood.repository.ItemIngredienteRepository;
import com.mycompany.backend_fastfood.repository.ItemRepository;
import com.mycompany.backend_fastfood.repository.PedidoRepository;
import com.mycompany.backend_fastfood.repository.ProductoIngredienteRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reglas de negocio de pedidos.
 *
 * Registro (sistema anterior):
 * - Lo registra un VENDEDOR; queda como vendedor del pedido.
 * - ID automático PE001..., fecha del día, estado PENDIENTE.
 * - Debe tener al menos un ítem.
 * - Solo productos activos y disponibles; cantidad mayor a cero.
 * - Descripción y precio del producto se copian al momento de la venta.
 * - Modificaciones: ingrediente activo y cantidad mayor a cero.
 *   QUITADO: no se puede quitar un ingrediente obligatorio, ni más
 *   unidades de las que tiene el producto.
 *   AGREGADO: cualquier ingrediente activo, esté o no en la receta.
 *
 * Reglas nuevas:
 * - Pedido, ítems y modificaciones se guardan en UNA transacción.
 * - Las modificaciones son por unidad del ítem.
 * - El precio de los ingredientes agregados se congela en la venta.
 * - IDs de ítems IT001... y de modificaciones II0001...
 *
 * Despacho: solo un COCINERO; solo PENDIENTE -> DESPACHADO.
 * Eliminación: solo pedidos PENDIENTE.
 * Reporte: pedidos DESPACHADO, con filtros opcionales de fecha y vendedor.
 */
@Slf4j
@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemRepository itemRepository;
    private final ItemIngredienteRepository itemIngredienteRepository;
    private final ProductoIngredienteRepository productoIngredienteRepository;
    private final EmpleadoRepository empleadoRepository;
    private final ProductoService productoService;
    private final IngredienteService ingredienteService;

    public PedidoService(
            PedidoRepository pedidoRepository,
            ItemRepository itemRepository,
            ItemIngredienteRepository itemIngredienteRepository,
            ProductoIngredienteRepository productoIngredienteRepository,
            EmpleadoRepository empleadoRepository,
            ProductoService productoService,
            IngredienteService ingredienteService) {

        this.pedidoRepository = pedidoRepository;
        this.itemRepository = itemRepository;
        this.itemIngredienteRepository = itemIngredienteRepository;
        this.productoIngredienteRepository = productoIngredienteRepository;
        this.empleadoRepository = empleadoRepository;
        this.productoService = productoService;
        this.ingredienteService = ingredienteService;
    }

    // ------------------------------------------------------------------
    // Consultas
    // ------------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAllByOrderByFechaAscIDAsc();
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPendientes() {
        return pedidoRepository.findByEstadoOrderByFechaAscIDAsc(
                EstadoPedido.PENDIENTE);
    }

    @Transactional(readOnly = true)
    public Pedido buscar(String id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException("pedido", id));
    }

    // ------------------------------------------------------------------
    // Registro
    // ------------------------------------------------------------------

    @Transactional
    public Pedido registrar(String usuarioVendedor, PedidoRequestDTO datos) {

        Empleado vendedor = obtenerEmpleadoConRol(
                usuarioVendedor, Rol.VENDEDOR,
                "Solo un vendedor puede registrar pedidos");

        if (datos.getItems() == null || datos.getItems().isEmpty()) {
            throw new DatoInvalidoException(
                    "El pedido debe tener al menos un producto");
        }

        // IDs: se parte de los existentes y se van sumando los nuevos,
        // porque todavía no están en la base.
        List<String> idsItems = new ArrayList<>(itemRepository.findAllIds());
        List<String> idsModificaciones =
                new ArrayList<>(itemIngredienteRepository.findAllIds());

        String idPedido = GeneradorId.siguiente(
                pedidoRepository.findAllIds(), "PE", 3);

        Pedido pedido = new Pedido(idPedido, LocalDate.now(), vendedor);

        int numeroItem = 0;

        for (PedidoRequestDTO.ItemRequest datosItem : datos.getItems()) {

            numeroItem++;

            Item item = crearItem(numeroItem, datosItem, pedido, idsItems);

            List<PedidoRequestDTO.ModificacionRequest> modificaciones =
                    datosItem.getModificaciones() != null
                            ? datosItem.getModificaciones()
                            : List.of();

            for (PedidoRequestDTO.ModificacionRequest datosMod : modificaciones) {
                agregarModificacion(numeroItem, datosMod, item, idsModificaciones);
            }

            pedido.agregarItem(item);
        }

        // Un solo save: guarda pedido, ítems y modificaciones juntos.
        Pedido guardado = pedidoRepository.save(pedido);

        log.info("Pedido {} registrado por {}: {} ítem(s), total ${}",
                idPedido, vendedor.getUsuario(),
                guardado.getItems().size(), guardado.calcularTotal());

        return guardado;
    }

    private Item crearItem(
            int numeroItem,
            PedidoRequestDTO.ItemRequest datos,
            Pedido pedido,
            List<String> idsItems) {

        String prefijo = "Ítem " + numeroItem + ": ";

        if (datos.getIdProducto() == null || datos.getIdProducto().isBlank()) {
            throw new DatoInvalidoException(
                    prefijo + "el ID del producto es obligatorio");
        }

        Producto producto = productoService.buscar(datos.getIdProducto().trim());

        if (!producto.isActivo()) {
            throw new ReglaNegocioException(
                    prefijo + "el producto " + producto.getDescripcion()
                    + " está dado de baja");
        }

        if (!producto.isDisponible()) {
            throw new ReglaNegocioException(
                    prefijo + "el producto " + producto.getDescripcion()
                    + " no está disponible");
        }

        if (datos.getCantidad() == null || datos.getCantidad() <= 0) {
            throw new DatoInvalidoException(
                    prefijo + "la cantidad debe ser mayor a cero");
        }

        String idItem = GeneradorId.siguiente(idsItems, "IT", 3);
        idsItems.add(idItem);

        return new Item(idItem, pedido, producto, datos.getCantidad());
    }

    private void agregarModificacion(
            int numeroItem,
            PedidoRequestDTO.ModificacionRequest datos,
            Item item,
            List<String> idsModificaciones) {

        String prefijo = "Ítem " + numeroItem + ": ";

        if (datos.getIdIngrediente() == null || datos.getIdIngrediente().isBlank()) {
            throw new DatoInvalidoException(
                    prefijo + "el ID del ingrediente es obligatorio");
        }

        Ingrediente ingrediente =
                ingredienteService.buscar(datos.getIdIngrediente().trim());

        if (!ingrediente.isActivo()) {
            throw new ReglaNegocioException(
                    prefijo + "el ingrediente " + ingrediente.getDescripcion()
                    + " está dado de baja");
        }

        TipoModificacion tipo = parsearTipo(prefijo, datos.getTipo());

        if (datos.getCantidad() == null || datos.getCantidad() <= 0) {
            throw new DatoInvalidoException(
                    prefijo + "la cantidad de " + ingrediente.getDescripcion()
                    + " debe ser mayor a cero");
        }

        int cantidad = datos.getCantidad();

        if (tipo == TipoModificacion.QUITADO) {
            validarQuitado(prefijo, item, ingrediente, cantidad);
        }

        String idMod = GeneradorId.siguiente(idsModificaciones, "II", 4);
        idsModificaciones.add(idMod);

        item.agregarModificacion(
                new ItemIngrediente(idMod, item, ingrediente, tipo, cantidad));
    }

    /**
     * Reglas del sistema anterior para quitar un ingrediente:
     * - no se puede quitar un ingrediente obligatorio;
     * - no se puede quitar más de lo que tiene el producto
     *   (receta + agregados - quitados ya cargados en este ítem).
     */
    private void validarQuitado(
            String prefijo,
            Item item,
            Ingrediente ingrediente,
            int cantidad) {

        Optional<ProductoIngrediente> relacion =
                productoIngredienteRepository.findById(new ProductoIngredienteId(
                        item.getProducto().getID(), ingrediente.getID()));

        if (relacion.isPresent() && relacion.get().isObligatorio()) {
            throw new ReglaNegocioException(
                    prefijo + "no se puede quitar " + ingrediente.getDescripcion()
                    + " porque es obligatorio");
        }

        int disponible = cantidadActual(item, ingrediente, relacion.isPresent());

        if (cantidad > disponible) {
            throw new ReglaNegocioException(
                    prefijo + "no se puede quitar " + cantidad + " de "
                    + ingrediente.getDescripcion() + ": el producto tiene "
                    + disponible);
        }
    }

    /** Cuántas unidades del ingrediente tiene el ítem por unidad, hasta ahora. */
    private int cantidadActual(Item item, Ingrediente ingrediente, boolean enReceta) {

        int cantidad = enReceta ? 1 : 0;

        for (ItemIngrediente mod : item.getModificaciones()) {

            if (!mod.getIngrediente().getID().equals(ingrediente.getID())) {
                continue;
            }

            if (mod.getTipoModificacion() == TipoModificacion.AGREGADO) {
                cantidad += mod.getCantidad();
            } else {
                cantidad -= mod.getCantidad();
            }
        }

        return Math.max(cantidad, 0);
    }

    // ------------------------------------------------------------------
    // Despacho
    // ------------------------------------------------------------------

    @Transactional
    public Pedido despachar(String usuarioCocinero, String idPedido) {

        Empleado cocinero = obtenerEmpleadoConRol(
                usuarioCocinero, Rol.COCINERO,
                "Solo un cocinero puede despachar pedidos");

        Pedido pedido = buscar(idPedido);

        if (!pedido.isPendiente()) {
            throw new ReglaNegocioException(
                    "El pedido " + idPedido + " ya fue despachado");
        }

        pedido.despachar(cocinero);
        Pedido guardado = pedidoRepository.save(pedido);

        log.info("Pedido {} despachado por {}", idPedido, cocinero.getUsuario());

        return guardado;
    }

    // ------------------------------------------------------------------
    // Eliminación
    // ------------------------------------------------------------------

    @Transactional
    public void eliminar(String idPedido) {

        Pedido pedido = buscar(idPedido);

        if (!pedido.isPendiente()) {
            throw new ReglaNegocioException(
                    "No se puede eliminar un pedido que ya fue despachado");
        }

        // Borra también ítems y modificaciones (cascade).
        pedidoRepository.delete(pedido);

        log.info("Pedido {} eliminado", idPedido);
    }

    // ------------------------------------------------------------------
    // Reporte de ventas
    // ------------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<Pedido> pedidosParaReporte(
            LocalDate desde,
            LocalDate hasta,
            String idVendedor) {

        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new DatoInvalidoException(
                    "La fecha 'desde' no puede ser posterior a 'hasta'");
        }

        String vendedor = (idVendedor == null || idVendedor.isBlank())
                ? null
                : idVendedor.trim();

        if (vendedor != null && !empleadoRepository.existsById(vendedor)) {
            throw new RecursoNoEncontradoException("empleado", vendedor);
        }

        return pedidoRepository.buscarParaReporte(
                EstadoPedido.DESPACHADO, desde, hasta, vendedor);
    }

    // ------------------------------------------------------------------
    // Auxiliares
    // ------------------------------------------------------------------

    private Empleado obtenerEmpleadoConRol(
            String usuario,
            Rol rolRequerido,
            String mensaje) {

        Empleado empleado = empleadoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new OperacionNoPermitidaException(
                        "Usuario autenticado no encontrado"));

        if (empleado.getRol() != rolRequerido) {
            throw new OperacionNoPermitidaException(mensaje);
        }

        return empleado;
    }

    private TipoModificacion parsearTipo(String prefijo, String tipoTexto) {

        if (tipoTexto == null || tipoTexto.isBlank()) {
            throw new DatoInvalidoException(
                    prefijo + "el tipo de modificación es obligatorio "
                    + "(AGREGADO o QUITADO)");
        }

        try {
            return TipoModificacion.valueOf(tipoTexto.trim().toUpperCase());

        } catch (IllegalArgumentException e) {
            throw new DatoInvalidoException(
                    prefijo + "tipo de modificación inválido: " + tipoTexto
                    + ". Valores permitidos: AGREGADO, QUITADO");
        }
    }
}