package com.mycompany.backend_fastfood.repository;

import com.mycompany.backend_fastfood.model.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductoRepository extends JpaRepository<Producto, String> {

    /**
     * Busca por ID. Devuelve null si no existe.
     * (Se usa en lugar de findById, que devuelve Optional.)
     */
    Producto findByID(String id);

    /** Para validar descripción única al registrar. */
    boolean existsByDescripcionIgnoreCase(String descripcion);

    /** Para validar descripción única al modificar (excluye al propio producto). */
    boolean existsByDescripcionIgnoreCaseAndIDNot(String descripcion, String id);

    /** Listado del gerente: todos los productos activos. */
    List<Producto> findByActivoTrue();

    /** Listado del vendedor: activos y disponibles para vender. */
    List<Producto> findByActivoTrueAndDisponibleTrue();

    /** IDs existentes, para generar el siguiente (ver GeneradorId). */
    @Query("SELECT p.ID FROM Producto p")
    List<String> findAllIds();
}