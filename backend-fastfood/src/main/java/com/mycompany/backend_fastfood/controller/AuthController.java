package com.mycompany.backend_fastfood.controller;

import com.mycompany.backend_fastfood.model.Empleado;
import com.mycompany.backend_fastfood.repository.EmpleadoRepository;
import com.mycompany.backend_fastfood.service.EmpleadoService;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Operaciones del usuario autenticado sobre sí mismo (cualquier rol).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final EmpleadoRepository empleadoRepository;
    private final EmpleadoService empleadoService;

    public AuthController(
            EmpleadoRepository empleadoRepository,
            EmpleadoService empleadoService) {

        this.empleadoRepository = empleadoRepository;
        this.empleadoService = empleadoService;
    }

    /**
     * Devuelve los datos del empleado autenticado (sin la contraseña:
     * ver Persona). Sirve para verificar usuario y contraseña desde el front.
     */
    @GetMapping("/me")
    public Empleado usuarioActual(Authentication authentication) {

        Empleado empleado =
                empleadoRepository.findByUsuario(authentication.getName());

        if (empleado == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario no encontrado");
        }

        return empleado;
    }

    /**
     * Cambia la contraseña del usuario autenticado.
     * Requiere la contraseña actual.
     *
     * Body:
     * { "passwordActual": "admin123", "passwordNueva": "otraClave456" }
     *
     * Ninguna entidad representa "cambiar la contraseña" (son dos
     * contraseñas, la actual y la nueva), así que el body se recibe
     * como un Map de texto a texto, sin crear una clase solo para esto.
     */
    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cambiarPassword(
            @RequestBody Map<String, String> datos,
            Authentication authentication) {

        empleadoService.cambiarPasswordPropia(
                authentication.getName(),
                datos.get("passwordActual"),
                datos.get("passwordNueva")
        );
    }
}