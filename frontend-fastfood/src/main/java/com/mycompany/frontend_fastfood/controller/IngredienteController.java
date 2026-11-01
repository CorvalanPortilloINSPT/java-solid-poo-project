package com.mycompany.frontend_fastfood.controller;

import com.mycompany.frontend_fastfood.cliente.BackendClient;
import com.mycompany.frontend_fastfood.cliente.BackendException;
import com.mycompany.frontend_fastfood.cliente.Sesion;
import com.mycompany.frontend_fastfood.dto.IngredienteDTO;
import com.mycompany.frontend_fastfood.dto.IngredienteRequestDTO;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Pantallas de ingredientes (solo gerente).
 *
 * Reemplaza a GerenteControlador: "Gestionar ingredientes"
 * (listar, registrar, modificar y dar de baja).
 *
 * Las reglas de negocio (descripción única, precio no negativo,
 * baja solo si no está en uso...) las valida el backend;
 * acá solo se muestran sus mensajes.
 */
@Controller
public class IngredienteController {

    private final BackendClient backendClient;

    public IngredienteController(BackendClient backendClient) {
        this.backendClient = backendClient;
    }

    // ------------------------------------------------------------------
    // Listado
    // ------------------------------------------------------------------

    /** Ingredientes activos. */
    @GetMapping("/ingredientes")
    public String listar(HttpSession sesion, Model model) {

        verificarGerente(sesion);

        List<IngredienteDTO> ingredientes = backendClient.getLista(
                "/api/ingredientes", IngredienteDTO[].class,
                Sesion.credenciales(sesion));

        model.addAttribute("ingredientes", ingredientes);

        return "ingredientesListado";
    }

    // ------------------------------------------------------------------
    // Alta
    // ------------------------------------------------------------------

    /** Formulario vacío. */
    @GetMapping("/ingredientes/nuevo")
    public String nuevo(HttpSession sesion, Model model) {

        verificarGerente(sesion);
        formulario(model, null, "", "");

        return "ingredienteFormulario";
    }

    /** Envía el alta al backend. */
    @PostMapping("/ingredientes/nuevo")
    public String registrar(
            @RequestParam String descripcion,
            @RequestParam String precio,
            HttpSession sesion,
            Model model,
            RedirectAttributes redirect) {

        verificarGerente(sesion);

        try {
            IngredienteDTO creado = backendClient.post(
                    "/api/ingredientes",
                    new IngredienteRequestDTO(descripcion, convertirPrecio(precio)),
                    IngredienteDTO.class,
                    Sesion.credenciales(sesion));

            redirect.addFlashAttribute("exito",
                    "Ingrediente " + creado.getId() + " registrado.");

            return "redirect:/ingredientes";

        } catch (BackendException e) {
            if (e.isNoAutenticado()) {
                throw e;
            }
            // Se vuelve a mostrar el formulario con lo que escribió el gerente.
            model.addAttribute("error", e.getMessage());
            formulario(model, null, descripcion, precio);
            return "ingredienteFormulario";
        }
    }

    // ------------------------------------------------------------------
    // Modificación
    // ------------------------------------------------------------------

    /** Formulario con los datos actuales. */
    @GetMapping("/ingredientes/{id}/editar")
    public String editar(@PathVariable String id, HttpSession sesion, Model model) {

        verificarGerente(sesion);

        IngredienteDTO ingrediente = backendClient.get(
                "/api/ingredientes/" + id, IngredienteDTO.class,
                Sesion.credenciales(sesion));

        formulario(model, ingrediente.getId(), ingrediente.getDescripcion(),
                precioComoTexto(ingrediente.getPrecio()));

        return "ingredienteFormulario";
    }

    /** Envía la modificación al backend. */
    @PostMapping("/ingredientes/{id}/editar")
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
                    "/api/ingredientes/" + id,
                    new IngredienteRequestDTO(descripcion, convertirPrecio(precio)),
                    IngredienteDTO.class,
                    Sesion.credenciales(sesion));

            redirect.addFlashAttribute("exito", "Ingrediente " + id + " modificado.");

            return "redirect:/ingredientes";

        } catch (BackendException e) {
            if (e.isNoAutenticado()) {
                throw e;
            }
            model.addAttribute("error", e.getMessage());
            formulario(model, id, descripcion, precio);
            return "ingredienteFormulario";
        }
    }

    // ------------------------------------------------------------------
    // Baja
    // ------------------------------------------------------------------

    /** Baja lógica. */
    @PostMapping("/ingredientes/{id}/baja")
    public String darDeBaja(
            @PathVariable String id,
            HttpSession sesion,
            RedirectAttributes redirect) {

        verificarGerente(sesion);

        try {
            backendClient.delete("/api/ingredientes/" + id, Sesion.credenciales(sesion));
            redirect.addFlashAttribute("exito", "Ingrediente " + id + " dado de baja.");

        } catch (BackendException e) {
            if (e.isNoAutenticado()) {
                throw e;
            }
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/ingredientes";
    }

    // ------------------------------------------------------------------
    // Auxiliares
    // ------------------------------------------------------------------

    /**
     * Datos del formulario. Si id es null, es un alta.
     * El precio va como texto para poder volver a mostrar
     * exactamente lo que escribió el usuario.
     */
    private void formulario(Model model, String id, String descripcion, String precio) {
        model.addAttribute("id", id);
        model.addAttribute("descripcion", descripcion);
        model.addAttribute("precio", precio);
    }

    /**
     * Texto del formulario -> Double para el backend.
     * Vacío -> null: el backend responde "El precio es obligatorio".
     * Acepta coma o punto decimal.
     */
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

    /** 500.0 -> "500"; 350.5 -> "350.5" (para el campo del formulario). */
    private String precioComoTexto(double precio) {
        return BigDecimal.valueOf(precio).stripTrailingZeros().toPlainString();
    }

    /** Solo el gerente administra ingredientes (igual que en el backend). */
    private void verificarGerente(HttpSession sesion) {
        if (!Sesion.tieneRol(sesion, "GERENTE")) {
            throw new BackendException(403, "Solo el gerente puede administrar ingredientes.");
        }
    }
}