package com.mycompany.backend_fastfood.controller;

import com.mycompany.backend_fastfood.dto.ProductoDTO;
import com.mycompany.backend_fastfood.dto.ProductoIngredienteDTO;
import com.mycompany.backend_fastfood.dto.ProductoRequestDTO;
import com.mycompany.backend_fastfood.dto.RecetaItemDTO;
import com.mycompany.backend_fastfood.model.Producto;
import com.mycompany.backend_fastfood.model.ProductoIngrediente;
import com.mycompany.backend_fastfood.service.ProductoService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de productos y sus recetas.
 * Toda la lógica está en ProductoService.
 *
 * El ID y la disponibilidad los calcula el sistema: el body de alta y
 * modificación (ProductoRequestDTO) solo tiene descripción y precio.
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // ------------------------------------------------------------------
    // Productos
    // ------------------------------------------------------------------

    /** Gerente: todos los productos activos. */
    @GetMapping
    public List<ProductoDTO> listarProductos() {
        return productoService.listarActivos()
                .stream()
                .map(ProductoController::aDTO)
                .toList();
    }

    /** Vendedor: solo los productos que se pueden vender. */
    @GetMapping("/disponibles")
    public List<ProductoDTO> listarDisponibles() {
        return productoService.listarDisponibles()
                .stream()
                .map(ProductoController::aDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ProductoDTO buscarProducto(@PathVariable String id) {
        return aDTO(productoService.buscar(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoDTO crearProducto(@RequestBody ProductoRequestDTO dto) {
        return aDTO(productoService.registrar(
                dto.getDescripcion(),
                dto.getPrecio()
        ));
    }

    @PutMapping("/{id}")
    public ProductoDTO modificarProducto(
            @PathVariable String id,
            @RequestBody ProductoRequestDTO dto) {

        return aDTO(productoService.modificar(
                id,
                dto.getDescripcion(),
                dto.getPrecio()
        ));
    }

    /** Baja lógica. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void darDeBajaProducto(@PathVariable String id) {
        productoService.darDeBaja(id);
    }

    // ------------------------------------------------------------------
    // Receta
    // ------------------------------------------------------------------

    @GetMapping("/{id}/ingredientes")
    public List<ProductoIngredienteDTO> verReceta(@PathVariable String id) {
        return productoService.verReceta(id)
                .stream()
                .map(ProductoController::aDTO)
                .toList();
    }

    @PostMapping("/{id}/ingredientes")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoIngredienteDTO agregarIngrediente(
            @PathVariable String id,
            @RequestBody RecetaItemDTO dto) {

        return aDTO(productoService.agregarIngrediente(
                id,
                dto.getIdIngrediente(),
                dto.isObligatorio()
        ));
    }

    @DeleteMapping("/{id}/ingredientes/{idIngrediente}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void quitarIngrediente(
            @PathVariable String id,
            @PathVariable String idIngrediente) {

        productoService.quitarIngrediente(id, idIngrediente);
    }

    // ------------------------------------------------------------------
    // Conversión a DTO
    // ------------------------------------------------------------------

    private static ProductoDTO aDTO(Producto producto) {
        return new ProductoDTO(
                producto.getID(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.isDisponible()
        );
    }

    private static ProductoIngredienteDTO aDTO(ProductoIngrediente relacion) {
        return new ProductoIngredienteDTO(
                relacion.getIDProducto(),
                relacion.getProducto().getDescripcion(),
                relacion.getIDIngrediente(),
                relacion.getIngrediente().getDescripcion(),
                relacion.isObligatorio()
        );
    }
}