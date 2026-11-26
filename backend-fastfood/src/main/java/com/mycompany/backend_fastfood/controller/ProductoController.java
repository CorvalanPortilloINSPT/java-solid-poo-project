package com.mycompany.backend_fastfood.controller;

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
 * Recibe y devuelve las entidades directamente (sin DTO).
 * El ID y la disponibilidad los calcula el sistema: del body de alta y
 * modificación solo se usan la descripción y el precio.
 * Body: { "descripcion": "Papas fritas", "precio": 900 }
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
    public List<Producto> listarProductos() {
        return productoService.listarActivos();
    }

    /** Vendedor: solo los productos que se pueden vender. */
    @GetMapping("/disponibles")
    public List<Producto> listarDisponibles() {
        return productoService.listarDisponibles();
    }

    @GetMapping("/{id}")
    public Producto buscarProducto(@PathVariable String id) {
        return productoService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Producto crearProducto(@RequestBody Producto datos) {

        return productoService.registrar(
                datos.getDescripcion(),
                datos.getPrecio()
        );
    }

    @PutMapping("/{id}")
    public Producto modificarProducto(
            @PathVariable String id,
            @RequestBody Producto datos) {

        return productoService.modificar(
                id,
                datos.getDescripcion(),
                datos.getPrecio()
        );
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
    public List<ProductoIngrediente> verReceta(@PathVariable String id) {
        return productoService.verReceta(id);
    }

    /**
     * Agrega un ingrediente a la receta.
     * Body: { "idIngrediente": "ING02", "obligatorio": false }
     * Si no se envía "obligatorio", se toma como false (opcional).
     */
    @PostMapping("/{id}/ingredientes")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoIngrediente agregarIngrediente(
            @PathVariable String id,
            @RequestBody ProductoIngrediente datos) {

        return productoService.agregarIngrediente(
                id,
                datos.getIdIngrediente(),
                datos.isObligatorio()
        );
    }

    @DeleteMapping("/{id}/ingredientes/{idIngrediente}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void quitarIngrediente(
            @PathVariable String id,
            @PathVariable String idIngrediente) {

        productoService.quitarIngrediente(id, idIngrediente);
    }
}
