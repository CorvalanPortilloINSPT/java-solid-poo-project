package com.mycompany.frontend_fastfood.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Body que el front envía al backend para dar de alta o modificar
 * un ingrediente (POST /api/ingredientes y PUT /api/ingredientes/{id}).
 *
 * Mismos campos que IngredienteRequestDTO del backend:
 * { "descripcion": "Panceta", "precio": 500 }
 *
 * El ID no está porque lo genera el backend.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IngredienteRequestDTO implements Serializable {

    private String descripcion;

    /** Double (no double), igual que en el backend: null = no se cargó. */
    private Double precio;
}