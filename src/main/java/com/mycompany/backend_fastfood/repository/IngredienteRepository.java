package com.mycompany.backend_fastfood.repository;

import com.mycompany.backend_fastfood.model.Ingrediente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredienteRepository extends JpaRepository<Ingrediente, String> {

    /** Para validar descripción única al registrar. */
    boolean existsByDescripcionIgnoreCase(String descripcion);

    /** Para validar descripción única al modificar (excluye al propio ingrediente). */
    boolean existsByDescripcionIgnoreCaseAndIDNot(String descripcion, String id);

    /** Listado para vendedores y gerente: solo los activos. */
    List<Ingrediente> findByActivoTrue();
}