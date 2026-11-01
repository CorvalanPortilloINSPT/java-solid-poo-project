package com.mycompany.frontend_fastfood.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Body que el front envía al backend para dar de alta o modificar
 * un producto (POST /api/productos y PUT /api/productos/{id}).
 *
 * Mismos campos que ProductoRequestDTO del backend:
 * { "descripcion": "Hamburguesa doble", "precio": 2500 }
 *
 * No lleva ID (lo genera el backend) ni "disponible"
 * (lo calcula el backend según la receta).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRequestDTO implements Serializable {

    private String descripcion;

    /** Double (no double), igual que en el backend: null = no se cargó. */
    private Double precio;
}