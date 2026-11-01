package com.mycompany.frontend_fastfood.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Producto tal como lo devuelve el backend
 * (GET /api/productos, /api/productos/disponibles y /api/productos/{id}).
 *
 * Mismos campos que ProductoDTO del backend.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductoDTO implements Serializable {

    private String id;
    private String descripcion;
    private double precio;
    private boolean disponible;
}