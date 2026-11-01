package com.mycompany.frontend_fastfood.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Body que el front envía al backend para agregar un ingrediente
 * a la receta de un producto (POST /api/productos/{id}/ingredientes).
 *
 * Mismos campos que RecetaItemDTO del backend:
 * { "idIngrediente": "ING03", "obligatorio": false }
 *
 * Para LEER la receta se usa ProductoIngredienteDTO
 * (igual que en el backend).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecetaItemDTO implements Serializable {

    private String idIngrediente;
    private boolean obligatorio;
}