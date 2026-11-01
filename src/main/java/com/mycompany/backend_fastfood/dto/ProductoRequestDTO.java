package com.mycompany.backend_fastfood.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Datos que envía el cliente para dar de alta o modificar un producto.
 *
 * Ejemplo de body:
 * { "descripcion": "Papas fritas", "precio": 900 }
 *
 * El ID y la disponibilidad los calcula el sistema, por eso no están acá.
 */
@Getter
@Setter
@NoArgsConstructor
public class ProductoRequestDTO {

    private String descripcion;

    /** Double (no double) para detectar si el cliente no lo envió. */
    private Double precio;
}