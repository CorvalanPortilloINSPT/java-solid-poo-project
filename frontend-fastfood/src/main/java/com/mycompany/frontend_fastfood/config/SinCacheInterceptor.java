package com.mycompany.frontend_fastfood.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Le indica al navegador que no guarde las páginas en su caché.
 *
 * Sin esto, después de cerrar sesión el botón "atrás" mostraba las
 * pantallas anteriores desde la caché del navegador, sin volver a
 * pedirlas al servidor, como si el usuario siguiera logueado.
 *
 * Con "no-store", al ir hacia atrás el navegador vuelve a pedir la
 * página: SesionInterceptor ve que no hay sesión y redirige al login.
 *
 * Se aplica a todas las páginas, también al login: así, si el usuario
 * vuelve hacia atrás hasta el login, el navegador lo pide de nuevo y
 * AuthController cierra la sesión. Se excluyen los estilos y las
 * imágenes, que sí conviene guardar en caché.
 */
@Component
public class SinCacheInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) {

        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");   // navegadores viejos (HTTP/1.0)
        response.setDateHeader("Expires", 0);        // ya vencida

        return true;
    }
}