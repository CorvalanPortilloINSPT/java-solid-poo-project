package com.mycompany.backend_fastfood.config;

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

    /**
     * Las expresiones con flecha (csrf -> ..., auth -> ...) son la forma
     * de configuración que exige Spring Security: cada método recibe un
     * "Customizer" que configura esa parte. Es el único lugar del backend
     * con programación funcional, porque el framework no ofrece otra
     * forma de escribir esta configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // Público: la página de error de Spring
                .requestMatchers("/error").permitAll()

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
     * Todas las contraseñas se guardan y se comparan con BCrypt.
     *
     * Se guardan con el prefijo del algoritmo: "{bcrypt}$2a$10$...".
     * DelegatingPasswordEncoder lee ese prefijo para saber con qué
     * algoritmo comparar; el único registrado es BCrypt, así que no se
     * acepta ninguna contraseña guardada en texto plano.
     * (El constructor de Spring recibe los algoritmos en un Map.)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {

        Map<String, PasswordEncoder> algoritmos = Map.of(
                "bcrypt", new BCryptPasswordEncoder());

        return new DelegatingPasswordEncoder("bcrypt", algoritmos);
    }
}