package com.mycompany.frontend_fastfood.cliente;

import com.mycompany.frontend_fastfood.dto.UsuarioActualDTO;
import jakarta.servlet.http.HttpSession;

/**
 * Acceso a los datos del usuario logueado guardados en la sesión HTTP.
 *
 * Centraliza los nombres de los atributos para no repetir textos
 * ("credenciales", "usuario") en cada controlador.
 */
public final class Sesion {

    private static final String CREDENCIALES = "credenciales";

    /** Nombre del atributo; los JSP lo leen como ${sessionScope.usuario}. */
    public static final String USUARIO = "usuario";

    private Sesion() {
        // Clase utilitaria: no se instancia.
    }

    public static void iniciar(
            HttpSession sesion,
            Credenciales credenciales,
            UsuarioActualDTO usuario) {

        sesion.setAttribute(CREDENCIALES, credenciales);
        sesion.setAttribute(USUARIO, usuario);
    }

    public static boolean estaIniciada(HttpSession sesion) {
        return sesion != null && sesion.getAttribute(CREDENCIALES) != null;
    }

    public static Credenciales credenciales(HttpSession sesion) {
        return (Credenciales) sesion.getAttribute(CREDENCIALES);
    }

    public static UsuarioActualDTO usuario(HttpSession sesion) {
        return (UsuarioActualDTO) sesion.getAttribute(USUARIO);
    }

    public static boolean tieneRol(HttpSession sesion, String rol) {
        UsuarioActualDTO usuario = usuario(sesion);
        return usuario != null && rol.equals(usuario.getRol());
    }

    public static void cerrar(HttpSession sesion) {
        sesion.invalidate();
    }
}