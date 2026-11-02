package com.mycompany.backend_fastfood.repository;

import com.mycompany.backend_fastfood.model.EstadoPedido;
import com.mycompany.backend_fastfood.model.Pedido;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PedidoRepository extends JpaRepository<Pedido, String> {

    /** Pedidos pendientes para el cocinero, del más viejo al más nuevo. */
    List<Pedido> findByEstadoOrderByFechaAscIDAsc(EstadoPedido estado);

    /** Todos los pedidos, ordenados (consulta del vendedor y del gerente). */
    List<Pedido> findAllByOrderByFechaAscIDAsc();

    /** Solo los IDs, para generar el siguiente (PE001, PE002...). */
    @Query("SELECT p.ID FROM Pedido p")
    List<String> findAllIds();

    /**
     * Reporte de ventas: pedidos en el estado indicado (DESPACHADO),
     * con filtros opcionales. Si un filtro llega en null, no se aplica.
     */
    @Query("""
            SELECT p FROM Pedido p
            WHERE p.estado = :estado
              AND (:desde IS NULL OR p.fecha >= :desde)
              AND (:hasta IS NULL OR p.fecha <= :hasta)
              AND (:idVendedor IS NULL OR p.empleadoVendedor.ID = :idVendedor)
            ORDER BY p.fecha ASC, p.ID ASC
            """)
    List<Pedido> buscarParaReporte(
            @Param("estado") EstadoPedido estado,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta,
            @Param("idVendedor") String idVendedor);
}