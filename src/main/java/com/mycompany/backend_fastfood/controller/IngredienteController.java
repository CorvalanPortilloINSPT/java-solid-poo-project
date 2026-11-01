package com.mycompany.backend_fastfood.controller;

import com.mycompany.backend_fastfood.dto.IngredienteDTO;
import com.mycompany.backend_fastfood.dto.IngredienteRequestDTO;
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
 * El ID lo genera el sistema: el body de alta y modificación
 * (IngredienteRequestDTO) solo tiene descripción y precio.
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
    public List<IngredienteDTO> listarIngredientes() {
        return ingredienteService.listarActivos()
                .stream()
                .map(IngredienteDTO::desde)
                .toList();
    }

    /** Busca por ID, aunque esté dado de baja (para el historial). */
    @GetMapping("/{id}")
    public IngredienteDTO buscarIngrediente(@PathVariable String id) {
        return IngredienteDTO.desde(ingredienteService.buscar(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IngredienteDTO crearIngrediente(
            @RequestBody IngredienteRequestDTO dto) {

        return IngredienteDTO.desde(ingredienteService.registrar(
                dto.getDescripcion(),
                dto.getPrecio()
        ));
    }

    @PutMapping("/{id}")
    public IngredienteDTO modificarIngrediente(
            @PathVariable String id,
            @RequestBody IngredienteRequestDTO dto) {

        return IngredienteDTO.desde(ingredienteService.modificar(
                id,
                dto.getDescripcion(),
                dto.getPrecio()
        ));
    }

    /** Baja lógica. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void darDeBajaIngrediente(@PathVariable String id) {
        ingredienteService.darDeBaja(id);
    }
}