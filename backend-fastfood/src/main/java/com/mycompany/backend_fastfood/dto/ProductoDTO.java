
package com.mycompany.backend_fastfood.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {

    private String id;
    private String descripcion;
    private double precio;
    private boolean disponible;
}
