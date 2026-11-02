package com.mycompany.backend_fastfood.service;

import com.mycompany.backend_fastfood.exception.DatoInvalidoException;
import com.mycompany.backend_fastfood.exception.RecursoNoEncontradoException;
import com.mycompany.backend_fastfood.exception.ReglaNegocioException;
import com.mycompany.backend_fastfood.model.EstadoPedido;
import com.mycompany.backend_fastfood.model.Ingrediente;
import com.mycompany.backend_fastfood.repository.IngredienteRepository;
import com.mycompany.backend_fastfood.repository.ItemIngredienteRepository;
import com.mycompany.backend_fastfood.repository.ProductoIngredienteRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reglas de negocio de ingredientes (tomadas del sistema anterior):
 * - El ID se genera automáticamente con formato ING01, ING02...
 * - La descripción es única, sin distinguir mayúsculas
 *   (también cuenta contra ingredientes dados de baja).
 * - El listado devuelve solo ingredientes activos.
 * - La baja es lógica (activo = false) y solo se permite si el
 *   ingrediente no forma parte de la receta de ningún producto activo
 *   ni de modificaciones de ítems en pedidos pendientes.
 *
 * Reglas agregadas en la API nueva:
 * - Descripción obligatoria.
 * - Precio obligatorio, numérico y no negativo.
 * - Un ingrediente dado de baja no se puede modificar
 *   (se conserva solo para el historial de ventas).
 */
@Slf4j
@Service
public class IngredienteService {

    private static final String PREFIJO_ID = "ING";
    private static final int DIGITOS_ID = 2;

    private final IngredienteRepository ingredienteRepository;
    private final ProductoIngredienteRepository productoIngredienteRepository;
    private final ItemIngredienteRepository itemIngredienteRepository;

    public IngredienteService(
            IngredienteRepository ingredienteRepository,
            ProductoIngredienteRepository productoIngredienteRepository,
            ItemIngredienteRepository itemIngredienteRepository) {

        this.ingredienteRepository = ingredienteRepository;
        this.productoIngredienteRepository = productoIngredienteRepository;
        this.itemIngredienteRepository = itemIngredienteRepository;
    }

    // ------------------------------------------------------------------
    // Consultas
    // ------------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<Ingrediente> listarActivos() {
        return ingredienteRepository.findByActivoTrue();
    }

    /** Busca por ID, aunque esté dado de baja (para el historial). */
    @Transactional(readOnly = true)
    public Ingrediente buscar(String id) {
        return ingredienteRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException("ingrediente", id));
    }

    // ------------------------------------------------------------------
    // Alta
    // ------------------------------------------------------------------

    @Transactional
    public Ingrediente registrar(String descripcion, Double precio) {

        String descripcionLimpia = validarDescripcion(descripcion);
        double precioValidado = validarPrecio(precio);

        if (ingredienteRepository.existsByDescripcionIgnoreCase(descripcionLimpia)) {
            throw new ReglaNegocioException(
                    "Ya existe un ingrediente con la descripción: "
                    + descripcionLimpia);
        }

        String id = generarNuevoId();

        Ingrediente ingrediente =
                new Ingrediente(id, descripcionLimpia, precioValidado);
        Ingrediente guardado = ingredienteRepository.save(ingrediente);

        log.info("Ingrediente registrado: {} - {}", id, descripcionLimpia);

        return guardado;
    }

    // ------------------------------------------------------------------
    // Modificación
    // ------------------------------------------------------------------

    @Transactional
    public Ingrediente modificar(String id, String descripcion, Double precio) {

        Ingrediente ingrediente = buscar(id);

        if (!ingrediente.isActivo()) {
            throw new ReglaNegocioException(
                    "No se puede modificar un ingrediente dado de baja");
        }

        String descripcionLimpia = validarDescripcion(descripcion);
        double precioValidado = validarPrecio(precio);

        if (ingredienteRepository.existsByDescripcionIgnoreCaseAndIDNot(
                descripcionLimpia, id)) {

            throw new ReglaNegocioException(
                    "Ya existe otro ingrediente con la descripción: "
                    + descripcionLimpia);
        }

        ingrediente.setDescripcion(descripcionLimpia);
        ingrediente.setPrecio(precioValidado);

        log.info("Ingrediente modificado: {}", id);

        return ingredienteRepository.save(ingrediente);
    }

    // ------------------------------------------------------------------
    // Baja lógica
    // ------------------------------------------------------------------

    @Transactional
    public void darDeBaja(String id) {

        Ingrediente ingrediente = buscar(id);

        if (!ingrediente.isActivo()) {
            throw new ReglaNegocioException(
                    "El ingrediente ya está dado de baja");
        }

        if (productoIngredienteRepository
                .existsByIngrediente_IDAndProducto_ActivoTrue(id)) {

            throw new ReglaNegocioException(
                    "No se puede dar de baja el ingrediente porque forma parte "
                    + "de la receta de uno o más productos activos. Primero debe "
                    + "quitarlo de esas recetas o dar de baja los productos.");
        }

        if (itemIngredienteRepository.existsByIngrediente_IDAndItem_Pedido_Estado(
                id, EstadoPedido.PENDIENTE)) {

            throw new ReglaNegocioException(
                    "No se puede dar de baja el ingrediente porque está "
                    + "implicado en pedidos pendientes.");
        }

        ingrediente.setActivo(false);
        ingredienteRepository.save(ingrediente);

        log.info("Ingrediente dado de baja: {}", id);
    }

    // ------------------------------------------------------------------
    // Auxiliares
    // ------------------------------------------------------------------

    private String generarNuevoId() {

        List<String> ids = ingredienteRepository.findAll()
                .stream()
                .map(Ingrediente::getID)
                .toList();

        String id = GeneradorId.siguiente(ids, PREFIJO_ID, DIGITOS_ID);

        if (ingredienteRepository.existsById(id)) {
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