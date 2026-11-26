package com.mycompany.frontend_fastfood.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Un ingrediente agregado o quitado en un ítem del pedido en armado,
 * por unidad del ítem. Lo usa ItemRequestDTO.
 *
 * Se envían al backend: idIngrediente, tipo y cantidad.
 * Los campos con @JsonIgnore son solo para mostrar en pantalla.
 */
@Getter
@Setter
@NoArgsConstructor
public class ModificacionRequestDTO implements Serializable {

    // --- Se envían al backend ---
    private String idIngrediente;
    private String tipo;
    private Integer cantidad;

    // --- Solo para mostrar ---
    @JsonIgnore
    private String descripcionIngrediente;

    @JsonIgnore
    private double precioUnitario;
}