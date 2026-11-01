package com.mycompany.backend_fastfood.service;

import com.mycompany.backend_fastfood.exception.DatoInvalidoException;
import com.mycompany.backend_fastfood.exception.RecursoNoEncontradoException;
import com.mycompany.backend_fastfood.exception.ReglaNegocioException;
import com.mycompany.backend_fastfood.model.EstadoPedido;
import com.mycompany.backend_fastfood.model.Ingrediente;
import com.mycompany.backend_fastfood.model.Producto;
import com.mycompany.backend_fastfood.model.ProductoIngrediente;
import com.mycompany.backend_fastfood.model.ProductoIngredienteId;
import com.mycompany.backend_fastfood.repository.ItemRepository;
import com.mycompany.backend_fastfood.repository.ProductoIngredienteRepository;
import com.mycompany.backend_fastfood.repository.ProductoRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reglas de negocio de productos y recetas.
 *
 * Productos:
 * - El ID se genera automáticamente con formato PR001, PR002...
 * - Descripción obligatoria y única, sin distinguir mayúsculas
 *   (también cuenta contra productos dados de baja).
 * - Precio numérico y no negativo.
 * - "disponible" lo calcula el sistema: es verdadero si la receta
 *   tiene al menos un ingrediente obligatorio. Un producto nuevo
 *   arranca no disponible.
 * - La baja es lógica y no se permite si el producto está en
 *   pedidos pendientes.
 * - Un producto dado de baja se conserva solo para el historial:
 *   no se modifica ni se le cambia la receta.
 *
 * Recetas:
 * - Solo se agregan ingredientes activos y sin repetir.
 * - Un ingrediente obligatorio no se puede quitar.
 * - Cada cambio en la receta recalcula la disponibilidad.
 */
@Slf4j
@Service
public class ProductoService {

    private static final String PREFIJO_ID = "PR";
    private static final int DIGITOS_ID = 3;

    private final ProductoRepository productoRepository;
    private final ProductoIngredienteRepository productoIngredienteRepository;
    private final ItemRepository itemRepository;
    private final IngredienteService ingredienteService;

    public ProductoService(
            ProductoRepository productoRepository,
            ProductoIngredienteRepository productoIngredienteRepository,
            ItemRepository itemRepository,
            IngredienteService ingredienteService) {

        this.productoRepository = productoRepository;
        this.productoIngredienteRepository = productoIngredienteRepository;
        this.itemRepository = itemRepository;
        this.ingredienteService = ingredienteService;
    }

    // ------------------------------------------------------------------
    // Consultas
    // ------------------------------------------------------------------

    /** Listado del gerente: todos los activos, disponibles o no. */
    @Transactional(readOnly = true)
    public List<Producto> listarActivos() {
        return productoRepository.findByActivoTrue();
    }

    /** Listado del vendedor: solo los que se pueden vender. */
    @Transactional(readOnly = true)
    public List<Producto> listarDisponibles() {
        return productoRepository.findByActivoTrueAndDisponibleTrue();
    }

    /** Busca por ID, aunque esté dado de baja (para el historial). */
    @Transactional(readOnly = true)
    public Producto buscar(String id) {
        return productoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException("producto", id));
    }

    // ------------------------------------------------------------------
    // Alta
    // ------------------------------------------------------------------

        @Transactional
    public Producto registrar(String descripcion, Double precio) {

        String descripcionLimpia = validarDescripcion(descripcion);
        double precioValidado = validarPrecio(precio);

        if (productoRepository.existsByDescripcionIgnoreCase(descripcionLimpia)) {
            throw new ReglaNegocioException(
                    "Ya existe un producto con la descripción: "
                    + descripcionLimpia);
        }

        String id = generarNuevoId();

        Producto producto = new Producto(id, descripcionLimpia, precioValidado, false);
        Producto guardado = productoRepository.save(producto);

        log.info("Producto registrado: {} - {}", id, descripcionLimpia);

        return guardado;
    }

    // ------------------------------------------------------------------
    // Modificación
    // ------------------------------------------------------------------

        @Transactional
    public Producto modificar(String id, String descripcion, Double precio) {

        Producto producto = buscar(id);
        verificarActivo(producto);

        String descripcionLimpia = validarDescripcion(descripcion);
        double precioValidado = validarPrecio(precio);

        if (productoRepository.existsByDescripcionIgnoreCaseAndIDNot(
                descripcionLimpia, id)) {

            throw new ReglaNegocioException(
                    "Ya existe otro producto con la descripción: "
                    + descripcionLimpia);
        }

        producto.setDescripcion(descripcionLimpia);
        producto.setPrecio(precioValidado);

        log.info("Producto modificado: {}", id);

        return productoRepository.save(producto);
    }

    // ------------------------------------------------------------------
    // Baja lógica
    // ------------------------------------------------------------------

    @Transactional
    public void darDeBaja(String id) {

        Producto producto = buscar(id);

        if (!producto.isActivo()) {
            throw new ReglaNegocioException(
                    "El producto ya está dado de baja");
        }

        if (itemRepository.existsByProducto_IDAndPedido_Estado(
                id, EstadoPedido.PENDIENTE)) {

            throw new ReglaNegocioException(
                    "No se puede dar de baja un producto incluido "
                    + "en pedidos pendientes");
        }

        producto.setActivo(false);
        productoRepository.save(producto);

        log.info("Producto dado de baja: {}", id);
    }

    // ------------------------------------------------------------------
    // Receta
    // ------------------------------------------------------------------

    /** Receta de un producto, aunque esté dado de baja (para el historial). */
    @Transactional(readOnly = true)
    public List<ProductoIngrediente> verReceta(String idProducto) {
        buscar(idProducto);
        return productoIngredienteRepository.findByProducto_ID(idProducto);
    }

    @Transactional
    public ProductoIngrediente agregarIngrediente(
            String idProducto,
            String idIngrediente,
            boolean obligatorio) {

        Producto producto = buscar(idProducto);
        verificarActivo(producto);

        if (idIngrediente == null || idIngrediente.isBlank()) {
            throw new DatoInvalidoException(
                    "El ID del ingrediente es obligatorio");
        }

        Ingrediente ingrediente = ingredienteService.buscar(idIngrediente);

        if (!ingrediente.isActivo()) {
            throw new ReglaNegocioException(
                    "No se puede agregar un ingrediente dado de baja a la receta");
        }

        ProductoIngredienteId clave =
                new ProductoIngredienteId(idProducto, idIngrediente);

        if (productoIngredienteRepository.existsById(clave)) {
            throw new ReglaNegocioException(
                    "El ingrediente " + ingrediente.getDescripcion()
                    + " ya forma parte de la receta");
        }

        ProductoIngrediente relacion =
                new ProductoIngrediente(producto, ingrediente, obligatorio);

        ProductoIngrediente guardada = productoIngredienteRepository.save(relacion);

        log.info("Ingrediente {} agregado a la receta de {} (obligatorio: {})",
                idIngrediente, idProducto, obligatorio);

        actualizarDisponibilidad(producto);

        return guardada;
    }

    @Transactional
    public void quitarIngrediente(String idProducto, String idIngrediente) {

        Producto producto = buscar(idProducto);
        verificarActivo(producto);

        ProductoIngrediente relacion = productoIngredienteRepository
                .findById(new ProductoIngredienteId(idProducto, idIngrediente))
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El ingrediente " + idIngrediente
                        + " no forma parte de la receta de " + idProducto));

        if (relacion.isObligatorio()) {
            throw new ReglaNegocioException(
                    "No se puede quitar un ingrediente obligatorio de la receta");
        }

        productoIngredienteRepository.delete(relacion);

        log.info("Ingrediente {} quitado de la receta de {}",
                idIngrediente, idProducto);

        actualizarDisponibilidad(producto);
    }

    // ------------------------------------------------------------------
    // Auxiliares
    // ------------------------------------------------------------------

    /**
     * Regla del sistema anterior: un producto está disponible si su
     * receta tiene al menos un ingrediente obligatorio.
     */
    private void actualizarDisponibilidad(Producto producto) {

        boolean disponible = productoIngredienteRepository
                .existsByProducto_IDAndObligatorioTrue(producto.getID());

        if (producto.isDisponible() != disponible) {
            producto.setDisponible(disponible);
            productoRepository.save(producto);

            log.info("Producto {} ahora está {}",
                    producto.getID(),
                    disponible ? "disponible" : "no disponible");
        }
    }

    private void verificarActivo(Producto producto) {
        if (!producto.isActivo()) {
            throw new ReglaNegocioException(
                    "El producto " + producto.getID() + " está dado de baja");
        }
    }

    private String generarNuevoId() {

        List<String> ids = productoRepository.findAll()
                .stream()
                .map(Producto::getID)
                .toList();

        String id = GeneradorId.siguiente(ids, PREFIJO_ID, DIGITOS_ID);

        if (productoRepository.existsById(id)) {
            throw new ReglaNegocioException(
                    "No se pudo generar un ID único. Intente nuevamente.");
        }

        return id;
    }

    private String validarDescripcion(String descripcion) {

        if (descripcion == null || descripcion.isBlank()) {
            throw new DatoInvalidoException("La descripción es obligatoria");
        }

        return descripcion.trim();
    }

        private double validarPrecio(Double precio) {

        if (precio == null) {
            throw new DatoInvalidoException("El precio es obligatorio");
        }

        if (!Double.isFinite(precio) || precio < 0) {
            throw new DatoInvalidoException(
                    "El precio debe ser un número válido y no negativo");
        }

        return precio;
    }
}