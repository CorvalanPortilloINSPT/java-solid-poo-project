package com.mycompany.backend_fastfood.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduce las excepciones de negocio a respuestas HTTP.
 *
 * Todas las respuestas de error tienen el mismo formato (ProblemDetail):
 * {
 *   "title": "Conflict",
 *   "status": 409,
 *   "detail": "Ya existe un ingrediente con la descripción: Pan",
 *   "instance": "/api/ingredientes"
 * }
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ProblemDetail manejarNoEncontrado(RecursoNoEncontradoException ex) {
        log.info("Recurso no encontrado: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DatoInvalidoException.class)
    public ProblemDetail manejarDatoInvalido(DatoInvalidoException ex) {
        log.info("Dato inválido: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ProblemDetail manejarReglaNegocio(ReglaNegocioException ex) {
        log.warn("Regla de negocio violada: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(OperacionNoPermitidaException.class)
    public ProblemDetail manejarOperacionNoPermitida(
            OperacionNoPermitidaException ex) {

        log.warn("Operación no permitida: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN, ex.getMessage());
    }
    /**
     * JSON mal formado o con tipos incorrectos
     * (por ejemplo "precio": "abc").
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail manejarJsonInvalido(HttpMessageNotReadableException ex) {
        log.info("JSON inválido: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "El cuerpo de la solicitud no es un JSON válido o tiene tipos incorrectos");
    }
}