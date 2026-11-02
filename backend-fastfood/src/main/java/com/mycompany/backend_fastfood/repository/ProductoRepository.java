package com.mycompany.backend_fastfood.repository;

import com.mycompany.backend_fastfood.model.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, String> {

    /** Para validar descripción única al registrar. */
    boolean existsByDescripcionIgnoreCase(String descripcion);

    /** Para validar descripción única al modificar (excluye al propio producto). */
    boolean existsByDescripcionIgnoreCaseAndIDNot(String descripcion, String id);

    /** Listado del gerente: todos los productos activos. */
    List<Producto> findByActivoTrue();

    /** Listado del vendedor: activos y disponibles para vender. */
    List<Producto> findByActivoTrueAndDisponibleTrue();
}