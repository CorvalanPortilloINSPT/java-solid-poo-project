package com.mycompany.frontend_fastfood.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Forma de los errores que devuelve el backend (ProblemDetail, armado
 * por su GlobalExceptionHandler). Por ejemplo:
 * { "title": "Conflict", "status": 409,
 *   "detail": "Ya existe un empleado con el DNI: 30111222" }
 *
 * BackendClient toma el "detail" para mostrarlo en la pantalla.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorBackendDTO {

    private String title;
    private int status;
    private String detail;
}