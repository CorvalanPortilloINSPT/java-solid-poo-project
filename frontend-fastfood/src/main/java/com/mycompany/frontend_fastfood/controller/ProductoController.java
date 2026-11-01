package com.mycompany.frontend_fastfood.controller;

import com.mycompany.frontend_fastfood.cliente.BackendClient;
import com.mycompany.frontend_fastfood.cliente.BackendException;
import com.mycompany.frontend_fastfood.cliente.Credenciales;
import com.mycompany.frontend_fastfood.cliente.Sesion;
import com.mycompany.frontend_fastfood.dto.IngredienteDTO;
import com.mycompany.frontend_fastfood.dto.ProductoDTO;
import com.mycompany.frontend_fastfood.dto.ProductoIngredienteDTO;
import com.mycompany.frontend_fastfood.dto.ProductoRequestDTO;
import com.mycompany.frontend_fastfood.dto.RecetaItemDTO;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Pantallas de productos y recetas (solo gerente).
 *
 * Reemplaza a GerenteControlador: "Gestionar productos"
 * (listar, registrar, modificar, dar de baja) y la carga de la receta.
 *
 * Las reglas de negocio (descripción única, disponibilidad según la
 * receta, ingredientes obligatorios...) las valida el backend;
 * acá solo se muestran sus mensajes.
 */
@Controller
public class ProductoController {

    private final BackendClient backendClient;

    public ProductoController(BackendClient backendClient) {
        this.backendClient = backendClient;
    }

    // ------------------------------------------------------------------
    // Listado
    // ------------------------------------------------------------------

    /** Productos activos, disponibles o no. */
    @GetMapping("/productos")
    public String listar(HttpSession sesion, Model model) {

        verificarGerente(sesion);

        List<ProductoDTO> productos = backendClient.getLista(
                "/api/productos", ProductoDTO[].class, Sesion.credenciales(sesion));

        model.addAttribute("productos", productos);

        return "productosListado";
    }

    // ------------------------------------------------------------------
    // Alta
    // ------------------------------------------------------------------

    /** Formulario vacío. */
    @GetMapping("/productos/nuevo")
    public String nuevo(HttpSession sesion, Model model) {

        verificarGerente(sesion);
        formulario(model, null, "", "");

        return "productoFormulario";
    }

    /**
     * Envía el alta al backend. Si sale bien, va directo a la receta:
     * un producto nuevo no está disponible hasta tener un ingrediente
     * obligatorio.
     */
    @PostMapping("/productos/nuevo")
    public String registrar(
            @RequestParam String descripcion,
            @RequestParam String precio,
            HttpSession sesion,
            Model model,
            RedirectAttributes redirect) {

        verificarGerente(sesion);

        try {
            ProductoDTO creado = backendClient.post(
                    "/api/productos",
                    new ProductoRequestDTO(descripcion, convertirPrecio(precio)),
                    ProductoDTO.class,
                    Sesion.credenciales(sesion));

            redirect.addFlashAttribute("exito", "Producto " + creado.getId()
                    + " registrado. Cargá la receta: el producto queda disponible"
                    + " cuando tiene al menos un ingrediente obligatorio.");

            return "redirect:/productos/" + creado.getId() + "/receta";

        } catch (BackendException e) {
            if (e.isNoAutenticado()) {
                throw e;
            }
            model.addAttribute("error", e.getMessage());
            formulario(model, null, descripcion, precio);
            return "productoFormulario";
        }
    }

    // ------------------------------------------------------------------
    // Modificación
    // ------------------------------------------------------------------

    /** Formulario con los datos actuales. */
    @GetMapping("/productos/{id}/editar")
    public String editar(@PathVariable String id, HttpSession sesion, Model model) {

        verificarGerente(sesion);

        ProductoDTO producto = backendClient.get(
                "/api/productos/" + id, ProductoDTO.class, Sesion.credenciales(sesion));

        formulario(model, producto.getId(), producto.getDescripcion(),
                precioComoTexto(producto.getPrecio()));

        return "productoFormulario";
    }

    /** Envía la modificación al backend. */
    @PostMapping("/productos/{id}/editar")
    public String modificar(
            @PathVariable String id,
            @RequestParam String descripcion,
            @RequestParam String precio,
            HttpSession sesion,
            Model model,
            RedirectAttributes redirect) {

        verificarGerente(sesion);

        try {
            backendClient.put(
                    "/api/productos/" + id,
                    new ProductoRequestDTO(descripcion, convertirPrecio(precio)),
                    ProductoDTO.class,
                    Sesion.credenciales(sesion));

            redirect.addFlashAttribute("exito", "Producto " + id + " modificado.");

            return "redirect:/productos";

        } catch (BackendException e) {
            if (e.isNoAutenticado()) {
                throw e;
            }
            model.addAttribute("error", e.getMessage());
            formulario(model, id, descripcion, precio);
            return "productoFormulario";
        }
    }

    // ------------------------------------------------------------------
    // Baja
    // ------------------------------------------------------------------

    /** Baja lógica. */
    @PostMapping("/productos/{id}/baja")
    public String darDeBaja(
            @PathVariable String id,
            HttpSession sesion,
            RedirectAttributes redirect) {

        verificarGerente(sesion);

        try {
            backendClient.delete("/api/productos/" + id, Sesion.credenciales(sesion));
            redirect.addFlashAttribute("exito", "Producto " + id + " dado de baja.");

        } catch (BackendException e) {
            if (e.isNoAutenticado()) {
                throw e;
            }
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/productos";
    }

    // ------------------------------------------------------------------
    // Receta
    // ------------------------------------------------------------------

    /** Receta del producto y formulario para agregar ingredientes. */
    @GetMapping("/productos/{id}/receta")
    public String receta(@PathVariable String id, HttpSession sesion, Model model) {

        verificarGerente(sesion);
        Credenciales credenciales = Sesion.credenciales(sesion);

        ProductoDTO producto = backendClient.get(
                "/api/productos/" + id, ProductoDTO.class, credenciales);

        List<ProductoIngredienteDTO> receta = backendClient.getLista(
                "/api/productos/" + id + "/ingredientes",
                ProductoIngredienteDTO[].class, credenciales);

        List<IngredienteDTO> activos = backendClient.getLista(
                "/api/ingredientes", IngredienteDTO[].class, credenciales);

        // En el desplegable solo se ofrecen los que todavía no están
        // en la receta (es solo presentación: el backend igual valida
        // que no se repitan).
        Set<String> enReceta = receta.stream()
                .map(ProductoIngredienteDTO::getIdIngrediente)
                .collect(Collectors.toSet());

        List<IngredienteDTO> paraAgregar = activos.stream()
                .filter(i -> !enReceta.contains(i.getId()))
                .toList();

        model.addAttribute("producto", producto);
        model.addAttribute("receta", receta);
        model.addAttribute("ingredientesParaAgregar", paraAgregar);

        return "productoReceta";
    }

    /** Agrega un ingrediente a la receta. */
    @PostMapping("/productos/{id}/receta")
    public String agregarIngrediente(
            @PathVariable String id,
            @RequestParam String idIngrediente,
            @RequestParam(defaultValue = "false") boolean obligatorio,
            HttpSession sesion,
            RedirectAttributes redirect) {

        verificarGerente(sesion);

        try {
            ProductoIngredienteDTO agregado = backendClient.post(
                    "/api/productos/" + id + "/ingredientes",
                    new RecetaItemDTO(idIngrediente, obligatorio),
                    ProductoIngredienteDTO.class,
                    Sesion.credenciales(sesion));

            redirect.addFlashAttribute("exito",
                    agregado.getDescripcionIngrediente() + " agregado a la receta.");

        } catch (BackendException e) {
            if (e.isNoAutenticado()) {
                throw e;
            }
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/productos/" + id + "/receta";
    }

    /** Quita un ingrediente (no obligatorio) de la receta. */
    @PostMapping("/productos/{id}/receta/{idIngrediente}/quitar")
    public String quitarIngrediente(
            @PathVariable String id,
            @PathVariable String idIngrediente,
            HttpSession sesion,
            RedirectAttributes redirect) {

        verificarGerente(sesion);

        try {
            backendClient.delete(
                    "/api/productos/" + id + "/ingredientes/" + idIngrediente,
                    Sesion.credenciales(sesion));

            redirect.addFlashAttribute("exito", "Ingrediente quitado de la receta.");

        } catch (BackendException e) {
            if (e.isNoAutenticado()) {
                throw e;
            }
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/productos/" + id + "/receta";
    }

    // ------------------------------------------------------------------
    // Auxiliares
    // ------------------------------------------------------------------

    /** Datos del formulario. Si id es null, es un alta. */
    private void formulario(Model model, String id, String descripcion, String precio) {
        model.addAttribute("id", id);
        model.addAttribute("descripcion", descripcion);
        model.addAttribute("precio", precio);
    }

    /** Texto del formulario -> Double. Vacío -> null. Acepta coma o punto. */
    private Double convertirPrecio(String texto) {

        if (texto == null || texto.isBlank()) {
            return null;
        }

        try {
            return Double.valueOf(texto.trim().replace(',', '.'));

        } catch (NumberFormatException e) {
            throw new BackendException(400, "El precio debe ser un número.");
        }
    }

    /** 1500.0 -> "1500" (para el campo del formulario). */
    private String precioComoTexto(double precio) {
        return BigDecimal.valueOf(precio).stripTrailingZeros().toPlainString();
    }

    /** Solo el gerente administra productos (igual que en el backend). */
    private void verificarGerente(HttpSession sesion) {
        if (!Sesion.tieneRol(sesion, "GERENTE")) {
            throw new BackendException(403, "Solo el gerente puede administrar productos.");
        }
    }
}