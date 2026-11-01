package com.mycompany.frontend_fastfood.controller;

import com.mycompany.frontend_fastfood.cliente.BackendClient;
import com.mycompany.frontend_fastfood.cliente.BackendException;
import com.mycompany.frontend_fastfood.cliente.Credenciales;
import com.mycompany.frontend_fastfood.cliente.Sesion;
import com.mycompany.frontend_fastfood.dto.CambioPasswordDTO;
import com.mycompany.frontend_fastfood.dto.UsuarioActualDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Login, logout y cambio de la propia contraseña.
 *
 * Mismo nombre que AuthController del backend, que tiene los dos
 * endpoints que se usan acá:
 * - GET /api/auth/me: el login no tiene endpoint propio en el backend;
 *   se valida llamando a /me con las credenciales ingresadas. Si responde
 *   200, son correctas y además devuelve los datos y el rol del empleado.
 * - PUT /api/auth/password: cambio de la propia contraseña (todos los roles).
 *
 * Reemplaza a LoginControlador del sistema anterior.
 *
 * Si alguien con la sesión iniciada vuelve a la página de login (por
 * ejemplo, con el botón "atrás" del navegador), la sesión se cierra:
 * volver al login equivale a salir. Como las páginas no se guardan en
 * caché (SinCacheInterceptor), el navegador siempre vuelve a pedir el
 * login y pasa por acá.
 *
 * Usuario y contraseña no admiten los caracteres < > { }: se rechazan
 * acá antes de llamar al backend (el formulario también los bloquea con
 * el atributo pattern, pero eso se puede saltear enviando el POST a mano).
 */
@Controller
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private static final String CARACTERES_PROHIBIDOS = "<>{}";
    private static final String MENSAJE_CARACTERES =
            "El usuario y la contraseña no pueden contener los caracteres < > { }.";

    private final BackendClient backendClient;

    public AuthController(BackendClient backendClient) {
        this.backendClient = backendClient;
    }

    // ------------------------------------------------------------------
    // Login y logout
    // ------------------------------------------------------------------

    /** La raíz del sitio lleva al menú o al login según haya sesión. */
    @GetMapping("/")
    public String raiz(HttpSession sesion) {
        return Sesion.estaIniciada(sesion) ? "redirect:/inicio" : "redirect:/login";
    }

    /**
     * Muestra el formulario de login. Si había una sesión iniciada
     * (el usuario volvió hacia atrás hasta el login), la cierra.
     */
    @GetMapping("/login")
    public String mostrarLogin(HttpSession sesion) {

        if (Sesion.estaIniciada(sesion)) {
            log.info("Sesión cerrada al volver al login: {}",
                    Sesion.usuario(sesion).getUsuario());

            Sesion.cerrar(sesion);
            return "redirect:/login?salir";
        }

        return "login";
    }

    @PostMapping("/login")
    public String iniciarSesion(
            @RequestParam String usuario,
            @RequestParam String password,
            HttpServletRequest request,
            Model model) {

        if (usuario.isBlank() || password.isEmpty()) {
            model.addAttribute("error", "Ingrese usuario y contraseña.");
            model.addAttribute("usuarioIngresado", usuario);
            return "login";
        }

        if (tieneCaracteresProhibidos(usuario) || tieneCaracteresProhibidos(password)) {
            log.warn("Intento de login con caracteres no permitidos");
            model.addAttribute("error", MENSAJE_CARACTERES);
            return "login";
        }

        Credenciales credenciales = new Credenciales(usuario.trim(), password);

        try {
            UsuarioActualDTO datos = backendClient.get(
                    "/api/auth/me", UsuarioActualDTO.class, credenciales);

            // Nuevo ID de sesión al iniciar sesión (evita "session fixation").
            request.getSession();
            request.changeSessionId();

            Sesion.iniciar(request.getSession(), credenciales, datos);

            log.info("Inicio de sesión: {} ({})", datos.getUsuario(), datos.getRol());

            return "redirect:/inicio";

        } catch (BackendException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("usuarioIngresado", usuario);
            return "login";
        }
    }

    @PostMapping("/logout")
    public String cerrarSesion(HttpSession sesion) {

        if (Sesion.estaIniciada(sesion)) {
            log.info("Cierre de sesión: {}", Sesion.usuario(sesion).getUsuario());
        }

        Sesion.cerrar(sesion);

        return "redirect:/login?salir";
    }

    // ------------------------------------------------------------------
    // Cambio de la propia contraseña (todos los roles)
    // ------------------------------------------------------------------

    @GetMapping("/password")
    public String mostrarCambioPassword() {
        return "cambioPassword";
    }

    /**
     * La confirmación ("repetir contraseña nueva") se controla acá porque
     * es solo de la pantalla: el backend recibe la actual y la nueva.
     * El resto de las reglas (actual correcta, nueva distinta...) las
     * valida el backend.
     */
    @PostMapping("/password")
    public String cambiarPassword(
            @RequestParam String passwordActual,
            @RequestParam String passwordNueva,
            @RequestParam String passwordRepetida,
            HttpSession sesion,
            Model model,
            RedirectAttributes redirect) {

        if (!passwordNueva.equals(passwordRepetida)) {
            model.addAttribute("error", "La contraseña nueva y su repetición no coinciden.");
            return "cambioPassword";
        }

        if (tieneCaracteresProhibidos(passwordNueva)) {
            model.addAttribute("error",
                    "La contraseña nueva no puede contener los caracteres < > { }.");
            return "cambioPassword";
        }

        Credenciales actuales = Sesion.credenciales(sesion);

        try {
            backendClient.putSinRespuesta(
                    "/api/auth/password",
                    new CambioPasswordDTO(passwordActual, passwordNueva),
                    actuales);

            // Las credenciales de la sesión tienen la contraseña vieja:
            // se reemplazan para que las próximas llamadas no den 401.
            Sesion.iniciar(sesion,
                    new Credenciales(actuales.usuario(), passwordNueva),
                    Sesion.usuario(sesion));

            log.info("Cambio de contraseña: {}", actuales.usuario());

            redirect.addFlashAttribute("exito", "Contraseña cambiada.");
            return "redirect:/password";

        } catch (BackendException e) {
            if (e.isNoAutenticado()) {
                throw e;
            }
            // Las contraseñas nunca se devuelven al formulario.
            model.addAttribute("error", e.getMessage());
            return "cambioPassword";
        }
    }

    // ------------------------------------------------------------------
    // Auxiliares
    // ------------------------------------------------------------------

    private static boolean tieneCaracteresProhibidos(String valor) {

        for (char c : CARACTERES_PROHIBIDOS.toCharArray()) {
            if (valor.indexOf(c) >= 0) {
                return true;
            }
        }

        return false;
    }
}