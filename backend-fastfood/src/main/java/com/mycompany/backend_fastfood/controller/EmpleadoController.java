package com.mycompany.backend_fastfood.controller;

import com.mycompany.backend_fastfood.model.Empleado;
import com.mycompany.backend_fastfood.service.EmpleadoService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de empleados. Toda la lógica está en EmpleadoService.
 *
 * Recibe y devuelve la entidad Empleado directamente (sin DTO). La
 * contraseña se recibe pero nunca se devuelve (ver Persona).
 *
 * Cada operación recibe el usuario autenticado, porque lo que se puede
 * hacer depende de quién lo pide:
 * - GERENTE: gestiona vendedores y cocineros, y asigna el rol del día.
 * - SYS_ADMIN: gestiona gerentes y SYS_ADMIN.
 */
@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    /** Solo los empleados que el usuario autenticado puede gestionar. */
    @GetMapping
    public List<Empleado> listarEmpleados(Authentication auth) {
        return empleadoService.listar(auth.getName());
    }

    @GetMapping("/{id}")
    public Empleado buscarEmpleado(
            @PathVariable String id,
            Authentication auth) {

        return empleadoService.buscar(auth.getName(), id);
    }

    /**
     * Alta. Body:
     * { "dni": "30111222", "nombre": "Ana", "apellido": "Gomez",
     *   "usuario": "agomez", "password": "secreta123", "rol": "VENDEDOR" }
     *
     * El objeto que llega solo trae los datos: el service valida y crea
     * el empleado (ID automático, contraseña con BCrypt, activo).
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Empleado registrarEmpleado(
            @RequestBody Empleado datos,
            Authentication auth) {

        return empleadoService.registrar(auth.getName(), datos);
    }

    /**
     * Modificación de datos personales. Body: nombre, apellido y usuario
     * obligatorios; "password" opcional (si no se envía, se mantiene).
     * El DNI y el rol no se modifican por esta vía.
     */
    @PutMapping("/{id}")
    public Empleado modificarEmpleado(
            @PathVariable String id,
            @RequestBody Empleado datos,
            Authentication auth) {

        return empleadoService.modificar(auth.getName(), id, datos);
    }

    /**
     * Rol del día: solo GERENTE, solo entre VENDEDOR y COCINERO.
     * Body: { "rol": "COCINERO" }. Del objeto que llega se usa solo el rol.
     */
    @PutMapping("/{id}/rol")
    public Empleado cambiarRol(
            @PathVariable String id,
            @RequestBody Empleado datos,
            Authentication auth) {

        return empleadoService.cambiarRol(auth.getName(), id, datos.getRol());
    }

    /** Baja lógica. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void darDeBajaEmpleado(
            @PathVariable String id,
            Authentication auth) {

        empleadoService.darDeBaja(auth.getName(), id);
    }
}
