package com.mycompany.frontend_fastfood.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Un ingrediente agregado o quitado en un ítem de un pedido ya
 * registrado, tal como lo devuelve el backend (la entidad
 * ItemIngrediente). Lo usa ItemDetalleDTO.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModificacionDetalleDTO {

    private String idIngrediente;
    private String descripcionIngrediente;
    private String tipo;

    /** Lo que se cargó: por cada unidad del ítem. */
    private int cantidadPorUnidad;

    /** Lo que tiene que preparar el cocinero: por unidad × cantidad del ítem. */
    private int cantidadTotal;

    /** Precio congelado al momento de la venta. */
    private double precioUnitario;
}