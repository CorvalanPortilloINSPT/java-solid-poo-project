package com.mycompany.backend_fastfood.repository;

import com.mycompany.backend_fastfood.model.EstadoPedido;
import com.mycompany.backend_fastfood.model.ItemIngrediente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ItemIngredienteRepository
        extends JpaRepository<ItemIngrediente, String> {

    /**
     * Baja de ingrediente: no se permite si está implicado en
     * modificaciones de ítems de pedidos con el estado indicado
     * (se usa con PENDIENTE).
     */
    boolean existsByIngrediente_IDAndItem_Pedido_Estado(
            String idIngrediente,
            EstadoPedido estado
    );

    /** Solo los IDs, para generar el siguiente (II0001, II0002...). */
    @Query("SELECT ii.ID FROM ItemIngrediente ii")
    List<String> findAllIds();
}