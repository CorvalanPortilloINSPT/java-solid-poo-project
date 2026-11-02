package com.mycompany.backend_fastfood.repository;

import com.mycompany.backend_fastfood.model.EstadoPedido;
import com.mycompany.backend_fastfood.model.Item;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ItemRepository extends JpaRepository<Item, String> {

    /**
     * Baja de producto: no se permite si el producto está
     * en pedidos con el estado indicado (se usa con PENDIENTE).
     */
    boolean existsByProducto_IDAndPedido_Estado(
            String productoId,
            EstadoPedido estado
    );

    /** Solo los IDs, para generar el siguiente (IT001, IT002...). */
    @Query("SELECT i.ID FROM Item i")
    List<String> findAllIds();
}