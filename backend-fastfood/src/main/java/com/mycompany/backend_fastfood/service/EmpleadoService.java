package com.mycompany.backend_fastfood.service;

import com.mycompany.backend_fastfood.exception.DatoInvalidoException;
import com.mycompany.backend_fastfood.exception.OperacionNoPermitidaException;
import com.mycompany.backend_fastfood.exception.RecursoNoEncontradoException;
import com.mycompany.backend_fastfood.exception.ReglaNegocioException;
import com.mycompany.backend_fastfood.model.Empleado;
import com.mycompany.backend_fastfood.model.Rol;
import com.mycompany.backend_fastfood.repository.EmpleadoRepository;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reglas de negocio de empleados.
 *
 * Quién gestiona a quién:
 * - GERENTE: gestiona VENDEDOR y COCINERO, y asigna el rol del día
 *   (vendedor o cocinero).
 * - SYS_ADMIN: gestiona GERENTE y SYS_ADMIN.
 *
 * Reglas (sistema anterior):
 * - ID automático con formato E0001, E0002...
 * - DNI y usuario únicos (también contra empleados dados de baja).
 * - El DNI no se modifica.
 * - Baja lógica; un empleado dado de baja no puede iniciar sesión.
 *
 * Reglas nuevas:
 * - Un empleado dado de baja no se modifica ni cambia de rol
 *   (se conserva solo para el historial de ventas).
 * - Nadie puede darse de baja a sí mismo.
 * - No se puede dar de baja al último GERENTE ni al último SYS_ADMIN activo.
 * - Las contraseñas nuevas se guardan con BCrypt.
 * - Al arrancar, si no hay ningún SYS_ADMIN activo se crea uno
 *   (sysadmin / sysadmin123), y si no hay ningún GERENTE activo
 *   se crea otro (admin / admin123).
 * - Cada usuario puede cambiar su propia contraseña, indicando la actual.
 * - Usuario, contraseña, nombre y apellido no admiten los caracteres
 *   < > { } (evita que se guarde código HTML o script en esos campos).
 */
@Slf4j
@Service
public class EmpleadoService {

    private static final String PREFIJO_ID = "E";
    private static final int DIGITOS_ID = 4;

    private static final Set<Rol> ROLES_DEL_DIA =
            EnumSet.of(Rol.VENDEDOR, Rol.COCINERO);

    /** Caracteres que no se aceptan en usuario, contraseña, nombre y apellido. */
    private static final String CARACTERES_PROHIBIDOS = "<>{}";

    private final EmpleadoRepository empleadoRepository;
    private final PasswordEncoder passwordEncoder;

    public EmpleadoService(
            EmpleadoRepository empleadoRepository,
            PasswordEncoder passwordEncoder) {

        this.empleadoRepository = empleadoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ------------------------------------------------------------------
    // Consultas
    // ------------------------------------------------------------------

    /** Lista solo los empleados que el usuario autenticado puede gestionar. */
    @Transactional(readOnly = true)
    public List<Empleado> listar(String usuarioActor) {

        Empleado actor = obtenerActor(usuarioActor);
        Set<Rol> gestionables = rolesGestionables(actor.getRol());

        if (gestionables.isEmpty()) {
            throw new OperacionNoPermitidaException(
                    "Su rol no permite gestionar empleados");
        }

        return empleadoRepository.findByRolIn(gestionables);
    }

    @Transactional(readOnly = true)
    public Empleado buscar(String usuarioActor, String id) {

        Empleado actor = obtenerActor(usuarioActor);
        Empleado empleado = buscarPorId(id);

        verificarGestionable(actor, empleado.getRol());

        return empleado;
    }

    // ------------------------------------------------------------------
    // Alta
    // ------------------------------------------------------------------

    @Transactional
    public Empleado registrar(String usuarioActor, Empleado datos) {

        Empleado actor = obtenerActor(usuarioActor);

        Rol rol = rolObligatorio(datos.getRol());
        verificarGestionable(actor, rol);

        String dni = obligatorio(datos.getDNI(), "El DNI");
        String nombre = obligatorio(datos.getNombre(), "El nombre");
        String apellido = obligatorio(datos.getApellido(), "El apellido");
        String usuario = obligatorio(datos.getUsuario(), "El usuario");
        // La contraseña llega en el JSON como "password" (ver Persona).
        String password = obligatorio(datos.getContraseña(), "La contraseña");

        sinCaracteresProhibidos(nombre, "El nombre");
        sinCaracteresProhibidos(apellido, "El apellido");
        sinCaracteresProhibidos(usuario, "El usuario");
        sinCaracteresProhibidos(password, "La contraseña");

        if (empleadoRepository.existsByDNI(dni)) {
            throw new ReglaNegocioException(
                    "Ya existe un empleado con el DNI: " + dni);
        }

        if (empleadoRepository.existsByUsuario(usuario)) {
            throw new ReglaNegocioException(
                    "Ya existe un empleado con el usuario: " + usuario);
        }

        String id = generarNuevoId();

        Empleado empleado = new Empleado(
                id,
                rol,
                dni,
                nombre,
                apellido,
                usuario,
                passwordEncoder.encode(password)
        );

        Empleado guardado = empleadoRepository.save(empleado);

        log.info("Empleado {} registrado por {}: {} ({})",
                id, actor.getUsuario(), usuario, rol);

        return guardado;
    }

    // ------------------------------------------------------------------
    // Modificación de datos personales
    // ------------------------------------------------------------------

    @Transactional
    public Empleado modificar(
            String usuarioActor,
            String id,
            Empleado datos) {

        Empleado actor = obtenerActor(usuarioActor);
        Empleado empleado = buscarPorId(id);

        verificarGestionable(actor, empleado.getRol());
        verificarActivo(empleado);

        // El DNI no se modifica: se acepta solo si no viene o es el mismo.
        if (datos.getDNI() != null
                && !datos.getDNI().trim().equals(empleado.getDNI())) {

            throw new DatoInvalidoException("El DNI no se puede modificar");
        }

        // El rol no se modifica por esta vía.
        if (datos.getRol() != null
                && datos.getRol() != empleado.getRol()) {

            throw new DatoInvalidoException(
                    "El rol no se modifica aquí. Use PUT /api/empleados/"
                    + id + "/rol");
        }

        String nombre = obligatorio(datos.getNombre(), "El nombre");
        String apellido = obligatorio(datos.getApellido(), "El apellido");
        String usuario = obligatorio(datos.getUsuario(), "El usuario");

        sinCaracteresProhibidos(nombre, "El nombre");
        sinCaracteresProhibidos(apellido, "El apellido");
        sinCaracteresProhibidos(usuario, "El usuario");

        if (empleadoRepository.existsByUsuarioAndIDNot(usuario, id)) {
            throw new ReglaNegocioException(
                    "Ya existe otro empleado con el usuario: " + usuario);
        }

        empleado.setNombre(nombre);
        empleado.setApellido(apellido);
        empleado.setUsuario(usuario);

        // La contraseña es opcional: si no viene, se mantiene la actual.
        String passwordNueva = datos.getContraseña();

        if (passwordNueva != null && !passwordNueva.isBlank()) {
            sinCaracteresProhibidos(passwordNueva, "La contraseña");
            empleado.setContraseña(passwordEncoder.encode(passwordNueva));
            log.info("Contraseña del empleado {} cambiada por {}",
                    id, actor.getUsuario());
        }

        log.info("Empleado {} modificado por {}", id, actor.getUsuario());

        return empleadoRepository.save(empleado);
    }

    // ------------------------------------------------------------------
    // Rol del día (consigna)
    // ------------------------------------------------------------------

    @Transactional
    public Empleado cambiarRol(String usuarioActor, String id, Rol rolPedido) {

        Empleado actor = obtenerActor(usuarioActor);

        if (actor.getRol() != Rol.GERENTE) {
            throw new OperacionNoPermitidaException(
                    "Solo un gerente puede asignar el rol del día");
        }

        Empleado empleado = buscarPorId(id);
        verificarActivo(empleado);

        if (!ROLES_DEL_DIA.contains(empleado.getRol())) {
            throw new OperacionNoPermitidaException(
                    "Solo se puede cambiar el rol de vendedores y cocineros");
        }

        Rol nuevoRol = rolObligatorio(rolPedido);

        if (!ROLES_DEL_DIA.contains(nuevoRol)) {
            throw new DatoInvalidoException(
                    "El rol del día solo puede ser VENDEDOR o COCINERO");
        }

        if (empleado.getRol() == nuevoRol) {
            return empleado;
        }

        Rol rolAnterior = empleado.getRol();
        empleado.setRol(nuevoRol);

        log.info("Rol del empleado {} cambiado de {} a {} por {}",
                id, rolAnterior, nuevoRol, actor.getUsuario());

        return empleadoRepository.save(empleado);
    }

    // ------------------------------------------------------------------
    // Contraseña propia (cualquier rol)
    // ------------------------------------------------------------------

    @Transactional
    public void cambiarPasswordPropia(
            String usuarioActor,
            String passwordActual,
            String passwordNueva) {

        Empleado actor = obtenerActor(usuarioActor);

        if (passwordActual == null || passwordActual.isEmpty()) {
            throw new DatoInvalidoException("La contraseña actual es obligatoria");
        }

        if (passwordNueva == null || passwordNueva.isBlank()) {
            throw new DatoInvalidoException("La contraseña nueva es obligatoria");
        }

        sinCaracteresProhibidos(passwordNueva, "La contraseña nueva");

        if (!passwordEncoder.matches(passwordActual, actor.getContraseña())) {
            log.warn("Cambio de contraseña rechazado para {}: "
                    + "la contraseña actual no coincide", usuarioActor);

            throw new DatoInvalidoException("La contraseña actual es incorrecta");
        }

        if (passwordEncoder.matches(passwordNueva, actor.getContraseña())) {
            throw new ReglaNegocioException(
                    "La contraseña nueva debe ser distinta de la actual");
        }

        actor.setContraseña(passwordEncoder.encode(passwordNueva));
        empleadoRepository.save(actor);

        log.info("El usuario {} cambió su contraseña", usuarioActor);
    }

    // ------------------------------------------------------------------
    // Baja lógica
    // ------------------------------------------------------------------

    @Transactional
    public void darDeBaja(String usuarioActor, String id) {

        Empleado actor = obtenerActor(usuarioActor);
        Empleado empleado = buscarPorId(id);

        verificarGestionable(actor, empleado.getRol());

        if (empleado.getID().equals(actor.getID())) {
            throw new ReglaNegocioException("No puede darse de baja a sí mismo");
        }

        if (!empleado.isActivo()) {
            throw new ReglaNegocioException("El empleado ya está dado de baja");
        }

        Rol rol = empleado.getRol();

        if ((rol == Rol.GERENTE || rol == Rol.SYS_ADMIN)
                && empleadoRepository.countByRolAndActivoTrue(rol) <= 1) {

            throw new ReglaNegocioException(
                    "No se puede dar de baja al último " + rol + " activo");
        }

        empleado.setActivo(false);
        empleadoRepository.save(empleado);

        log.info("Empleado {} dado de baja por {}", id, actor.getUsuario());
    }

    // ------------------------------------------------------------------
    // Usuarios iniciales
    // ------------------------------------------------------------------

    /**
     * Se llama al arrancar (InicializadorDatos). Garantiza que siempre
     * haya alguien que pueda administrar el sistema:
     * - si no hay ningún SYS_ADMIN activo, crea sysadmin / sysadmin123;
     * - si no hay ningún GERENTE activo, crea admin / admin123.
     * Si ese usuario o DNI ya existen (por ejemplo, dado de baja),
     * no lo crea y lo informa en el log.
     */
    @Transactional
    public void crearUsuariosInicialesSiFaltan() {

        crearInicialSiFalta(Rol.SYS_ADMIN, "00000000",
                "Administrador", "Sistema", "sysadmin", "sysadmin123");

        crearInicialSiFalta(Rol.GERENTE, "00000001",
                "Gerente", "General", "admin", "admin123");
    }

    private void crearInicialSiFalta(
            Rol rol,
            String dni,
            String nombre,
            String apellido,
            String usuario,
            String password) {

        if (empleadoRepository.countByRolAndActivoTrue(rol) > 0) {
            return;
        }

        if (empleadoRepository.existsByUsuario(usuario)
                || empleadoRepository.existsByDNI(dni)) {

            log.warn("No hay ningún {} activo, pero el usuario '{}' o el DNI {} "
                    + "ya existen: no se crea el usuario inicial.", rol, usuario, dni);
            return;
        }

        Empleado inicial = new Empleado(
                generarNuevoId(),
                rol,
                dni,
                nombre,
                apellido,
                usuario,
                passwordEncoder.encode(password)
        );

        empleadoRepository.save(inicial);

        log.warn("No había ningún {} activo: se creó el usuario inicial '{}'. "
                + "Cambie la contraseña por defecto.", rol, usuario);
    }

    // ------------------------------------------------------------------
    // Auxiliares
    // ------------------------------------------------------------------

    private Set<Rol> rolesGestionables(Rol rolActor) {

        if (rolActor == Rol.GERENTE) {
            return ROLES_DEL_DIA;
        }

        if (rolActor == Rol.SYS_ADMIN) {
            return EnumSet.of(Rol.GERENTE, Rol.SYS_ADMIN);
        }

        return EnumSet.noneOf(Rol.class);
    }

    private void verificarGestionable(Empleado actor, Rol rolObjetivo) {

        if (rolesGestionables(actor.getRol()).contains(rolObjetivo)) {
            return;
        }

        String mensaje;

        if (actor.getRol() == Rol.GERENTE) {
            mensaje = "Un gerente solo puede gestionar vendedores y cocineros";
        } else if (actor.getRol() == Rol.SYS_ADMIN) {
            mensaje = "Un SYS_ADMIN solo puede gestionar gerentes y SYS_ADMIN";
        } else {
            mensaje = "Su rol no permite gestionar empleados";
        }

        throw new OperacionNoPermitidaException(mensaje);
    }

    private Empleado obtenerActor(String usuarioActor) {

        Empleado actor = empleadoRepository.findByUsuario(usuarioActor);

        if (actor == null) {
            throw new OperacionNoPermitidaException(
                    "Usuario autenticado no encontrado");
        }

        return actor;
    }

    private Empleado buscarPorId(String id) {

        Empleado empleado = empleadoRepository.findByID(id);

        if (empleado == null) {
            throw new RecursoNoEncontradoException("empleado", id);
        }

        return empleado;
    }

    private void verificarActivo(Empleado empleado) {
        if (!empleado.isActivo()) {
            throw new ReglaNegocioException(
                    "El empleado " + empleado.getID() + " está dado de baja");
        }
    }

    /**
     * El rol llega en el JSON como texto ("VENDEDOR") y Jackson lo
     * convierte al enum Rol. Si el texto no es un rol válido, Jackson
     * rechaza el JSON (400, ver GlobalExceptionHandler); acá solo se
     * controla que se haya enviado.
     */
    private Rol rolObligatorio(Rol rol) {

        if (rol == null) {
            throw new DatoInvalidoException("El rol es obligatorio");
        }

        return rol;
    }

    private String obligatorio(String valor, String campo) {

        if (valor == null || valor.isBlank()) {
            throw new DatoInvalidoException(campo + " es obligatorio");
        }

        return valor.trim();
    }

    /**
     * Rechaza los caracteres < > { }. No se usan en nombres ni usuarios
     * reales, y bloquearlos evita que se guarde código HTML o script.
     * (Además, las vistas muestran todo escapado con c:out / fn:escapeXml.)
     */
    private void sinCaracteresProhibidos(String valor, String campo) {

        for (char c : CARACTERES_PROHIBIDOS.toCharArray()) {
            if (valor.indexOf(c) >= 0) {
                throw new DatoInvalidoException(
                        campo + " no puede contener los caracteres < > { }");
            }
        }
    }

    private String generarNuevoId() {

        List<String> ids = empleadoRepository.findAllIds();

        String id = GeneradorId.siguiente(ids, PREFIJO_ID, DIGITOS_ID);

        if (empleadoRepository.existsById(id)) {
            throw new ReglaNegocioException(
                    "No se pudo generar un ID único. Intente nuevamente.");
        }

        return id;
    }
}