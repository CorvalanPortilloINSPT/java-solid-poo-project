package com.mycompany.frontend_fastfood.controller;

import com.mycompany.frontend_fastfood.cliente.BackendClient;
import com.mycompany.frontend_fastfood.cliente.BackendException;
import com.mycompany.frontend_fastfood.cliente.Credenciales;
import com.mycompany.frontend_fastfood.cliente.Sesion;
import com.mycompany.frontend_fastfood.dto.CambioRolDTO;
import com.mycompany.frontend_fastfood.dto.EmpleadoDTO;
import com.mycompany.frontend_fastfood.dto.EmpleadoRequestDTO;
import com.mycompany.frontend_fastfood.dto.UsuarioActualDTO;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Pantallas de empleados (gerente y SYS_ADMIN).
 *
 * Reemplaza a GerenteControlador: "Gestionar empleados" y
 * "Asignar roles del día".
 *
 * Quién gestiona a quién lo decide el backend:
 * - GERENTE: vendedores y cocineros; además asigna el rol del día.
 * - SYS_ADMIN: gerentes y SYS_ADMIN.
 * El front solo adapta lo que muestra (roles del alta, columna de rol del día).
 */
@Controller
public class EmpleadoController {

    private static final List<String> ROLES_GERENTE = List.of("VENDEDOR", "COCINERO");
    private static final List<String> ROLES_SYS_ADMIN = List.of("GERENTE", "SYS_ADMIN");

    private final BackendClient backendClient;

    public EmpleadoController(BackendClient backendClient) {
        this.backendClient = backendClient;
    }

    // ------------------------------------------------------------------
    // Listado
    // ------------------------------------------------------------------

    /** Empleados que el usuario logueado puede gestionar (activos y dados de baja). */
    @GetMapping("/empleados")
    public String listar(HttpSession sesion, Model model) {

        verificarGestor(sesion);

        List<EmpleadoDTO> empleados = backendClient.getLista(
                "/api/empleados", EmpleadoDTO[].class, Sesion.credenciales(sesion));

        model.addAttribute("empleados", empleados);
        model.addAttribute("puedeAsignarRolDelDia", Sesion.tieneRol(sesion, "GERENTE"));
        model.addAttribute("rolesDelDia", ROLES_GERENTE);
        model.addAttribute("idUsuarioActual", Sesion.usuario(sesion).getId());

        return "empleadosListado";
    }

    // ------------------------------------------------------------------
    // Alta
    // ------------------------------------------------------------------

    /** Formulario vacío. */
    @GetMapping("/empleados/nuevo")
    public String nuevo(HttpSession sesion, Model model) {

        verificarGestor(sesion);
        formularioAlta(model, sesion, new EmpleadoDTO());

        return "empleadoFormulario";
    }

    /** Envía el alta al backend. */
    @PostMapping("/empleados/nuevo")
    public String registrar(
            @RequestParam String dni,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String usuario,
            @RequestParam String password,
            @RequestParam(required = false) String rol,
            HttpSession sesion,
            Model model,
            RedirectAttributes redirect) {

        verificarGestor(sesion);

        try {
            EmpleadoDTO creado = backendClient.post(
                    "/api/empleados",
                    new EmpleadoRequestDTO(dni, nombre, apellido, usuario, password, rol),
                    EmpleadoDTO.class,
                    Sesion.credenciales(sesion));

            redirect.addFlashAttribute("exito",
                    "Empleado " + creado.getId() + " (" + creado.getUsuario() + ") registrado.");

            return "redirect:/empleados";

        } catch (BackendException e) {
            if (e.isNoAutenticado()) {
                throw e;
            }
            // Se vuelve a mostrar el formulario con los datos cargados,
            // salvo la contraseña (nunca se devuelve al navegador).
            EmpleadoDTO cargado = new EmpleadoDTO();
            cargado.setDni(dni);
            cargado.setNombre(nombre);
            cargado.setApellido(apellido);
            cargado.setUsuario(usuario);
            cargado.setRol(rol);

            model.addAttribute("error", e.getMessage());
            formularioAlta(model, sesion, cargado);
            return "empleadoFormulario";
        }
    }

    // ------------------------------------------------------------------
    // Modificación
    // ------------------------------------------------------------------

    /** Formulario con los datos actuales. */
    @GetMapping("/empleados/{id}/editar")
    public String editar(@PathVariable String id, HttpSession sesion, Model model) {

        verificarGestor(sesion);

        EmpleadoDTO empleado = backendClient.get(
                "/api/empleados/" + id, EmpleadoDTO.class, Sesion.credenciales(sesion));

        model.addAttribute("empleado", empleado);
        model.addAttribute("esAlta", false);

        return "empleadoFormulario";
    }

    /**
     * Envía la modificación. DNI y rol van en null (no se cambian acá);
     * la contraseña vacía va en null (se mantiene la actual).
     */
    @PostMapping("/empleados/{id}/editar")
    public String modificar(
            @PathVariable String id,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String usuario,
            @RequestParam(required = false) String password,
            HttpSession sesion,
            Model model,
            RedirectAttributes redirect) {

        verificarGestor(sesion);

        String passwordNueva = (password == null || password.isBlank()) ? null : password;

        try {
            EmpleadoDTO modificado = backendClient.put(
                    "/api/empleados/" + id,
                    new EmpleadoRequestDTO(null, nombre, apellido, usuario, passwordNueva, null),
                    EmpleadoDTO.class,
                    Sesion.credenciales(sesion));

            // Un SYS_ADMIN puede modificarse a sí mismo: si cambió su usuario
            // o su contraseña, se actualizan las credenciales de la sesión
            // para que las próximas llamadas al backend sigan funcionando.
            if (id.equals(Sesion.usuario(sesion).getId())) {
                actualizarSesion(sesion, modificado.getUsuario(), passwordNueva);
            }

            redirect.addFlashAttribute("exito", "Empleado " + id + " modificado.");

            return "redirect:/empleados";

        } catch (BackendException e) {
            if (e.isNoAutenticado()) {
                throw e;
            }
            // Se vuelve a pedir el empleado para mostrar DNI y rol,
            // y se pisan los campos editables con lo que escribió el usuario.
            EmpleadoDTO empleado = backendClient.get(
                    "/api/empleados/" + id, EmpleadoDTO.class, Sesion.credenciales(sesion));
            empleado.setNombre(nombre);
            empleado.setApellido(apellido);
            empleado.setUsuario(usuario);

            model.addAttribute("error", e.getMessage());
            model.addAttribute("empleado", empleado);
            model.addAttribute("esAlta", false);
            return "empleadoFormulario";
        }
    }

    // ------------------------------------------------------------------
    // Rol del día (solo gerente)
    // ------------------------------------------------------------------

    /** Cambia el rol del día: VENDEDOR <-> COCINERO. */
    @PostMapping("/empleados/{id}/rol")
    public String cambiarRol(
            @PathVariable String id,
            @RequestParam String rol,
            HttpSession sesion,
            RedirectAttributes redirect) {

        verificarGestor(sesion);

        try {
            EmpleadoDTO modificado = backendClient.put(
                    "/api/empleados/" + id + "/rol",
                    new CambioRolDTO(rol),
                    EmpleadoDTO.class,
                    Sesion.credenciales(sesion));

            redirect.addFlashAttribute("exito", "Empleado " + id
                    + " ahora es " + modificado.getRol() + ".");

        } catch (BackendException e) {
            if (e.isNoAutenticado()) {
                throw e;
            }
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/empleados";
    }

    // ------------------------------------------------------------------
    // Baja
    // ------------------------------------------------------------------

    /** Baja lógica. */
    @PostMapping("/empleados/{id}/baja")
    public String darDeBaja(
            @PathVariable String id,
            HttpSession sesion,
            RedirectAttributes redirect) {

        verificarGestor(sesion);

        try {
            backendClient.delete("/api/empleados/" + id, Sesion.credenciales(sesion));
            redirect.addFlashAttribute("exito", "Empleado " + id + " dado de baja.");

        } catch (BackendException e) {
            if (e.isNoAutenticado()) {
                throw e;
            }
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/empleados";
    }

    // ------------------------------------------------------------------
    // Auxiliares
    // ------------------------------------------------------------------

    /** Datos del formulario de alta; los roles dependen de quién está logueado. */
    private void formularioAlta(Model model, HttpSession sesion, EmpleadoDTO empleado) {
        model.addAttribute("empleado", empleado);
        model.addAttribute("esAlta", true);
        model.addAttribute("roles",
                Sesion.tieneRol(sesion, "GERENTE") ? ROLES_GERENTE : ROLES_SYS_ADMIN);
    }

    /**
     * Si el usuario logueado cambió su propio usuario o contraseña,
     * guarda las credenciales nuevas en la sesión y vuelve a pedir
     * sus datos al backend con ellas.
     */
    private void actualizarSesion(HttpSession sesion, String usuarioNuevo, String passwordNueva) {

        Credenciales actuales = Sesion.credenciales(sesion);
        Credenciales nuevas = new Credenciales(
                usuarioNuevo,
                passwordNueva != null ? passwordNueva : actuales.password());

        UsuarioActualDTO datos = backendClient.get(
                "/api/auth/me", UsuarioActualDTO.class, nuevas);

        Sesion.iniciar(sesion, nuevas, datos);
    }

    /** Solo gerente y SYS_ADMIN gestionan empleados (igual que en el backend). */
    private void verificarGestor(HttpSession sesion) {
        if (!Sesion.tieneRol(sesion, "GERENTE") && !Sesion.tieneRol(sesion, "SYS_ADMIN")) {
            throw new BackendException(403,
                    "Solo el gerente o el administrador del sistema pueden gestionar empleados.");
        }
    }
}