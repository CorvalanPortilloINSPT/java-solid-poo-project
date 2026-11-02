package com.mycompany.backend_fastfood.controller;

import com.mycompany.backend_fastfood.dto.CambioRolDTO;
import com.mycompany.backend_fastfood.dto.EmpleadoDTO;
import com.mycompany.backend_fastfood.dto.EmpleadoRequestDTO;
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
    public List<EmpleadoDTO> listarEmpleados(Authentication auth) {
        return empleadoService.listar(auth.getName())
                .stream()
                .map(EmpleadoDTO::desde)
                .toList();
    }

    @GetMapping("/{id}")
    public EmpleadoDTO buscarEmpleado(
            @PathVariable String id,
            Authentication auth) {

        return EmpleadoDTO.desde(
                empleadoService.buscar(auth.getName(), id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmpleadoDTO registrarEmpleado(
            @RequestBody EmpleadoRequestDTO dto,
            Authentication auth) {

        return EmpleadoDTO.desde(
                empleadoService.registrar(auth.getName(), dto));
    }

    @PutMapping("/{id}")
    public EmpleadoDTO modificarEmpleado(
            @PathVariable String id,
            @RequestBody EmpleadoRequestDTO dto,
            Authentication auth) {

        return EmpleadoDTO.desde(
                empleadoService.modificar(auth.getName(), id, dto));
    }

    /** Rol del día: solo GERENTE, solo entre VENDEDOR y COCINERO. */
    @PutMapping("/{id}/rol")
    public EmpleadoDTO cambiarRol(
            @PathVariable String id,
            @RequestBody CambioRolDTO dto,
            Authentication auth) {

        return EmpleadoDTO.desde(
                empleadoService.cambiarRol(auth.getName(), id, dto.getRol()));
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