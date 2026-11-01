package com.mycompany.frontend_fastfood.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Ingrediente tal como lo devuelve el backend
 * (GET /api/ingredientes y GET /api/ingredientes/{id}).
 *
 * Mismos campos que IngredienteDTO del backend.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class IngredienteDTO implements Serializable {

    private String id;
    private String descripcion;
    private double precio;
    private boolean activo;
}