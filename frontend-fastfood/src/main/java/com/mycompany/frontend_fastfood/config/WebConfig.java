package com.mycompany.frontend_fastfood.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de Spring MVC del front.
 *
 * Registra dos interceptores, que se ejecutan en este orden:
 * 1. SinCacheInterceptor: todas las páginas (salvo estilos e imágenes)
 *    se envían con "no guardar en caché", para que el botón "atrás"
 *    no muestre pantallas después de cerrar sesión.
 * 2. SesionInterceptor: exige sesión iniciada, salvo en las páginas que
 *    se pueden ver sin haber iniciado sesión.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final SinCacheInterceptor sinCacheInterceptor;
    private final SesionInterceptor sesionInterceptor;

    public WebConfig(
            SinCacheInterceptor sinCacheInterceptor,
            SesionInterceptor sesionInterceptor) {

        this.sinCacheInterceptor = sinCacheInterceptor;
        this.sesionInterceptor = sesionInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(sinCacheInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/img/**");

        registry.addInterceptor(sesionInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/",          // decide solo si va al login o al menú
                        "/login",     // formulario y envío del login
                        "/logout",    // si la sesión ya venció, igual vuelve al login
                        "/prueba",    // página de prueba de conexión
                        "/error",     // página de error de Spring
                        "/css/**",    // estilos (si los agregamos)
                        "/img/**");   // imágenes (si las agregamos)
    }
}