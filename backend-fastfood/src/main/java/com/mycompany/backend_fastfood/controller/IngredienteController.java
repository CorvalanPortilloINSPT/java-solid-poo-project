package com.mycompany.backend_fastfood.controller;

import com.mycompany.backend_fastfood.model.Ingrediente;
import com.mycompany.backend_fastfood.service.IngredienteService;
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
 * Endpoints de ingredientes. Toda la lógica está en IngredienteService.
 *
 * Recibe y devuelve la entidad Ingrediente directamente (sin DTO).
 * El ID lo genera el sistema: del body de alta y modificación solo se
 * usan la descripción y el precio.
 * Body: { "descripcion": "Panceta", "precio": 500 }
 */
@RestController
@RequestMapping("/api/ingredientes")
public class IngredienteController {

    private final IngredienteService ingredienteService;

    public IngredienteController(IngredienteService ingredienteService) {
        this.ingredienteService = ingredienteService;
    }

    /** Lista solo los ingredientes activos. */
    @GetMapping
    public List<Ingrediente> listarIngredientes() {
        return ingredienteService.listarActivos();
    }

    /** Busca por ID, aunque esté dado de baja (para el historial). */
    @GetMapping("/{id}")
    public Ingrediente buscarIngrediente(@PathVariable String id) {
        return ingredienteService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ingrediente crearIngrediente(@RequestBody Ingrediente datos) {

        return ingredienteService.registrar(
                datos.getDescripcion(),
                datos.getPrecio()
        );
    }

    @PutMapping("/{id}")
    public Ingrediente modificarIngrediente(
            @PathVariable String id,
            @RequestBody Ingrediente datos) {

        return ingredienteService.modificar(
                id,
                datos.getDescripcion(),
                datos.getPrecio()
        );
    }

    /** Baja lógica. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void darDeBajaIngrediente(@PathVariable String id) {
        ingredienteService.darDeBaja(id);
    }
}
