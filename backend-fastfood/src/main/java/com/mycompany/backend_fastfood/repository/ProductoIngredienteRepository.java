package com.mycompany.backend_fastfood.repository;

import com.mycompany.backend_fastfood.model.ProductoIngrediente;
import com.mycompany.backend_fastfood.model.ProductoIngredienteId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoIngredienteRepository
        extends JpaRepository<ProductoIngrediente, ProductoIngredienteId> {

    /** Receta completa de un producto. */
    List<ProductoIngrediente> findByProducto_ID(String idProducto);

    /**
     * Disponibilidad: un producto está disponible si su receta
     * tiene al menos un ingrediente obligatorio.
     */
    boolean existsByProducto_IDAndObligatorioTrue(String idProducto);

    /**
     * Baja de ingrediente: la receta solo bloquea la baja
     * si el producto que la contiene está activo.
     */
    boolean existsByIngrediente_IDAndProducto_ActivoTrue(String idIngrediente);
}