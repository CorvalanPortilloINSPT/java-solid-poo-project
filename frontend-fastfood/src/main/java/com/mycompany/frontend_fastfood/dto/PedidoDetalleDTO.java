package com.mycompany.frontend_fastfood.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Detalle completo de un pedido, tal como lo devuelve el backend
 * (la entidad Pedido): pedido → ítems (ItemDetalleDTO) →
 * modificaciones (ModificacionDetalleDTO).
 *
 * Jackson arma los tres niveles automáticamente a partir del JSON.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PedidoDetalleDTO {

    private String id;
    private LocalDate fecha;
    private String estado;
    private String idVendedor;
    private String nombreVendedor;
    private String idCocinero;
    private String nombreCocinero;
    private List<ItemDetalleDTO> items = new ArrayList<>();
    private double total;
}