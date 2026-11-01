package com.mycompany.frontend_fastfood.config;

import com.mycompany.frontend_fastfood.cliente.Sesion;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Se ejecuta antes de cada controlador. Si no hay sesión iniciada,
 * redirige al login y el controlador no llega a ejecutarse.
 *
 * Las páginas que no necesitan sesión (login, prueba, error) se
 * excluyen en WebConfig.
 */
@Component
public class SesionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        // getSession(false): no crea una sesión nueva si no existe.
        if (Sesion.estaIniciada(request.getSession(false))) {
            return true;
        }

        response.sendRedirect(request.getContextPath() + "/login");
        return false;
    }
}