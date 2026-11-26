package com.mycompany.frontend_fastfood.cliente;

import com.mycompany.frontend_fastfood.dto.ErrorBackendDTO;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Único punto de contacto entre el front y la API del backend.
 *
 * - Agrega usuario y contraseña (HTTP Basic) en cada llamada.
 * - Convierte el JSON en objetos Java y viceversa (Jackson, incluido en
 *   Spring Web).
 * - Traduce cualquier error a BackendException, con un mensaje listo
 *   para mostrar en la página.
 *
 * Si hubiera que cambiar la forma de leer o escribir el JSON, solo se
 * modifica esta clase.
 */
@Slf4j
@Component
public class BackendClient {

    private final RestTemplate restTemplate;
    private final String backendUrl;

    public BackendClient(
            RestTemplate restTemplate,
            @Value("${backend.url}") String backendUrl) {

        this.restTemplate = restTemplate;
        this.backendUrl = backendUrl;
    }

    // ------------------------------------------------------------------
    // Operaciones
    // ------------------------------------------------------------------

    /** GET que devuelve un objeto. Ejemplo: get("/api/auth/me", UsuarioDTO.class, cred) */
    public <T> T get(String ruta, Class<T> tipo, Credenciales credenciales) {
        return llamar(HttpMethod.GET, ruta, null, tipo, credenciales);
    }

    /**
     * GET que devuelve una lista. Se pide como arreglo y se pasa a una lista.
     * Ejemplo: getLista("/api/productos", ProductoDTO[].class, cred)
     *
     * Se pide como arreglo porque RestTemplate no puede saber el tipo de los
     * elementos de una List<T> (los genéricos se borran al compilar); el
     * arreglo, en cambio, sí conserva el tipo de sus elementos.
     */
    public <T> List<T> getLista(String ruta, Class<T[]> tipoArreglo, Credenciales credenciales) {
        T[] arreglo = llamar(HttpMethod.GET, ruta, null, tipoArreglo, credenciales);

        List<T> lista = new ArrayList<>();

        // Si el backend no devolvió nada, queda la lista vacía.
        if (arreglo != null) {
            for (T elemento : arreglo) {
                lista.add(elemento);
            }
        }

        return lista;
    }

    /** POST con body; devuelve lo que responde el backend. */
    public <T> T post(String ruta, Object body, Class<T> tipo, Credenciales credenciales) {
        return llamar(HttpMethod.POST, ruta, body, tipo, credenciales);
    }

    /** PUT con body (puede ser null, por ejemplo para despachar). */
    public <T> T put(String ruta, Object body, Class<T> tipo, Credenciales credenciales) {
        return llamar(HttpMethod.PUT, ruta, body, tipo, credenciales);
    }

    /** PUT cuya respuesta no tiene body (204), por ejemplo cambiar la contraseña. */
    public void putSinRespuesta(String ruta, Object body, Credenciales credenciales) {
        llamar(HttpMethod.PUT, ruta, body, Void.class, credenciales);
    }

    /** DELETE (el backend responde 204, sin body). */
    public void delete(String ruta, Credenciales credenciales) {
        llamar(HttpMethod.DELETE, ruta, null, Void.class, credenciales);
    }

    // ------------------------------------------------------------------
    // Implementación común
    // ------------------------------------------------------------------

    private <T> T llamar(
            HttpMethod metodo,
            String ruta,
            Object body,
            Class<T> tipo,
            Credenciales credenciales) {

        HttpHeaders headers = new HttpHeaders();

        if (credenciales != null) {
            headers.setBasicAuth(
                    credenciales.usuario(),
                    credenciales.password(),
                    StandardCharsets.UTF_8);
        }

        if (body != null) {
            headers.setContentType(MediaType.APPLICATION_JSON);
        }

        HttpEntity<Object> request = new HttpEntity<>(body, headers);

        try {
            return restTemplate
                    .exchange(backendUrl + ruta, metodo, request, tipo)
                    .getBody();

        } catch (HttpStatusCodeException e) {
            int status = e.getStatusCode().value();
            String mensaje = mensajeDeError(e, status);

            log.warn("{} {} respondió {}: {}", metodo, ruta, status, mensaje);
            throw new BackendException(status, mensaje);

        } catch (ResourceAccessException e) {
            log.error("{} {}: sin conexión con el backend: {}",
                    metodo, ruta, e.getMessage());
            throw new BackendException(BackendException.SIN_CONEXION,
                    "No se pudo conectar con el backend. Verifique que esté iniciado.");

        } catch (RestClientException e) {
            log.error("{} {}: respuesta inesperada: {}", metodo, ruta, e.getMessage());
            throw new BackendException(500,
                    "El backend devolvió una respuesta inesperada.");
        }
    }

    /**
     * Usa el "detail" del ProblemDetail que arma el GlobalExceptionHandler
     * del backend. Si no hay body (401, 403 de SecurityConfig), usa un
     * mensaje según el código.
     */
    private String mensajeDeError(HttpStatusCodeException e, int status) {

        try {
            ErrorBackendDTO error = e.getResponseBodyAs(ErrorBackendDTO.class);

            if (error != null && error.getDetail() != null && !error.getDetail().isBlank()) {
                return error.getDetail();
            }
        } catch (RuntimeException ignorada) {
            // El body no era un ProblemDetail: se usa el mensaje por defecto.
        }

        switch (status) {
            case 401:
                return "Usuario o contraseña incorrectos, o el empleado está dado de baja.";
            case 403:
                return "No tiene permiso para realizar esta operación.";
            case 404:
                return "No se encontró lo solicitado.";
            default:
                return "El backend respondió con un error (" + status + ").";
        }
    }
}