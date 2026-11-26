package com.mycompany.backend_fastfood.repository;

import com.mycompany.backend_fastfood.model.Ingrediente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IngredienteRepository extends JpaRepository<Ingrediente, String> {

    /**
     * Busca por ID. Devuelve null si no existe.
     * (Se usa en lugar de findById, que devuelve Optional.)
     */
    Ingrediente findByID(String id);

    /** Para validar descripción única al registrar. */
    boolean existsByDescripcionIgnoreCase(String descripcion);

    /** Para validar descripción única al modificar (excluye al propio ingrediente). */
    boolean existsByDescripcionIgnoreCaseAndIDNot(String descripcion, String id);

    /** Listado para vendedores y gerente: solo los activos. */
    List<Ingrediente> findByActivoTrue();

    /** IDs existentes, para generar el siguiente (ver GeneradorId). */
    @Query("SELECT i.ID FROM Ingrediente i")
    List<String> findAllIds();
}