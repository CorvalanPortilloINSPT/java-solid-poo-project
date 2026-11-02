package com.mycompany.backend_fastfood.dto;

import com.mycompany.backend_fastfood.model.Ingrediente;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Datos de un ingrediente que devuelve la API.
 */
@Getter
@AllArgsConstructor
public class IngredienteDTO {

    private String id;
    private String descripcion;
    private double precio;
    private boolean activo;

    public static IngredienteDTO desde(Ingrediente ingrediente) {
        return new IngredienteDTO(
                ingrediente.getID(),
                ingrediente.getDescripcion(),
                ingrediente.getPrecio(),
                ingrediente.isActivo()
        );
    }
}