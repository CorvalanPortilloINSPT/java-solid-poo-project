package com.mycompany.backend_fastfood.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Datos que envía el cliente para dar de alta o modificar un ingrediente.
 *
 * Ejemplo de body:
 * { "descripcion": "Panceta", "precio": 500 }
 *
 * El ID lo genera el sistema, por eso no está acá.
 */
@Getter
@Setter
@NoArgsConstructor
public class IngredienteRequestDTO {

    private String descripcion;

    /** Double (no double) para detectar si el cliente no lo envió. */
    private Double precio;
}