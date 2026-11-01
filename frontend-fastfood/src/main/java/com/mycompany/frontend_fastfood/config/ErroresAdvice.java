package com.mycompany.frontend_fastfood.config;

import com.mycompany.frontend_fastfood.cliente.BackendException;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Último recurso para los errores del backend que ningún controlador
 * capturó (por ejemplo, al cargar una página).
 *
 * - 401 en medio de la navegación (empleado dado de baja o contraseña
 *   cambiada desde otro lado): se cierra la sesión y se vuelve al login.
 * - Cualquier otro error: se muestra la página error.jsp con el mensaje.
 *
 * Es el equivalente del GlobalExceptionHandler del backend, pero en vez
 * de devolver JSON muestra una página.
 */
@ControllerAdvice
public class ErroresAdvice {

    @ExceptionHandler(BackendException.class)
    public String manejarBackend(BackendException e, HttpSession sesion, Model model) {

        if (e.isNoAutenticado()) {
            sesion.invalidate();
            return "redirect:/login";
        }

        model.addAttribute("mensaje", e.getMessage());
        model.addAttribute("sinConexion", e.isSinConexion());
        return "errorBackend";
    }
}