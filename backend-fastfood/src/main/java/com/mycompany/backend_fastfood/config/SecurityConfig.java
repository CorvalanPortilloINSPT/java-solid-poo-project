package com.mycompany.backend_fastfood.config;

import com.mycompany.backend_fastfood.security.TextoPlanoLegacyEncoder;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Seguridad de la API.
 *
 * Autenticación: HTTP Basic contra los empleados de la base
 * (ver EmpleadoUserDetailsService).
 *
 * Permisos por rol:
 * - GERENTE: administra el negocio (productos, ingredientes, pedidos,
 *   reportes), gestiona vendedores y cocineros, y asigna el rol del día.
 * - VENDEDOR: registra pedidos; consulta productos, ingredientes y pedidos.
 * - COCINERO: consulta pedidos pendientes y los despacha.
 * - SYS_ADMIN: gestiona gerentes y otros SYS_ADMIN; no opera el negocio.
 *
 * Hay dos capas de permisos:
 * - Esta clase decide por URL y rol ("¿puede entrar acá?").
 * - Los services deciden sobre qué registro puede operar cada rol
 *   ("¿puede operar sobre este empleado?").
 *
 * El orden de las reglas importa: se aplica la PRIMERA que coincide.
 * Cualquier endpoint que no esté listado acá queda BLOQUEADO (denyAll).
 * Al crear un endpoint nuevo hay que agregar su regla.
 */
@Configuration
public class SecurityConfig {

    private static final String GERENTE = "GERENTE";
    private static final String VENDEDOR = "VENDEDOR";
    private static final String COCINERO = "COCINERO";
    private static final String SYS_ADMIN = "SYS_ADMIN";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // Públicos
                .requestMatchers("/api/prueba", "/error").permitAll()

                // Datos y contraseña del usuario logueado (cualquier rol)
                .requestMatchers("/api/auth/**").authenticated()

                // Empleados:
                // - rol del día: solo gerente
                // - resto: gerente y sys_admin (el service decide sobre quién
                //   puede operar cada uno)
                .requestMatchers(HttpMethod.PUT, "/api/empleados/*/rol")
                    .hasRole(GERENTE)
                .requestMatchers("/api/empleados/**")
                    .hasAnyRole(GERENTE, SYS_ADMIN)

                // Ingredientes: el vendedor consulta, el gerente administra
                .requestMatchers(HttpMethod.GET, "/api/ingredientes/**")
                    .hasAnyRole(GERENTE, VENDEDOR)
                .requestMatchers("/api/ingredientes/**").hasRole(GERENTE)

                // Productos: el vendedor consulta, el gerente administra
                .requestMatchers(HttpMethod.GET, "/api/productos/**")
                    .hasAnyRole(GERENTE, VENDEDOR)
                .requestMatchers("/api/productos/**").hasRole(GERENTE)

                // Pedidos
                // - reporte de ventas: solo gerente
                .requestMatchers(HttpMethod.GET, "/api/pedidos/reporte")
                    .hasRole(GERENTE)
                // - listado general: gerente y vendedor
                .requestMatchers(HttpMethod.GET, "/api/pedidos")
                    .hasAnyRole(GERENTE, VENDEDOR)
                // - pendientes y detalle: los tres roles
                .requestMatchers(HttpMethod.GET, "/api/pedidos/**")
                    .hasAnyRole(GERENTE, VENDEDOR, COCINERO)
                // - registrar: solo vendedor
                .requestMatchers(HttpMethod.POST, "/api/pedidos")
                    .hasRole(VENDEDOR)
                // - despachar: solo cocinero
                .requestMatchers(HttpMethod.PUT, "/api/pedidos/*/despacho")
                    .hasRole(COCINERO)
                // - eliminar pendientes: solo gerente
                .requestMatchers(HttpMethod.DELETE, "/api/pedidos/*")
                    .hasRole(GERENTE)

                // Todo lo demás, bloqueado
                .anyRequest().denyAll()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    /**
     * Contraseñas nuevas: se guardan con BCrypt ("{bcrypt}$2a$...").
     * Contraseñas viejas en texto plano (sin prefijo): se siguen aceptando,
     * así los empleados cargados desde la consola pueden iniciar sesión
     * y la base sigue siendo compatible con el sistema anterior.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {

        Map<String, PasswordEncoder> encoders = Map.of(
                "bcrypt", new BCryptPasswordEncoder());

        DelegatingPasswordEncoder encoder =
                new DelegatingPasswordEncoder("bcrypt", encoders);

        encoder.setDefaultPasswordEncoderForMatches(
                new TextoPlanoLegacyEncoder());

        return encoder;
    }
}