package com.mycompany.backend_fastfood.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductoIngredienteDTO {

    private String idProducto;
    private String descripcionProducto;
    private String idIngrediente;
    private String descripcionIngrediente;
    private boolean obligatorio;
}