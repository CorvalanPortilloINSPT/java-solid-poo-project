package com.mycompany.backend_fastfood.controller;

import com.mycompany.backend_fastfood.dto.CambioPasswordDTO;
import com.mycompany.backend_fastfood.dto.UsuarioActualDTO;
import com.mycompany.backend_fastfood.repository.EmpleadoRepository;
import com.mycompany.backend_fastfood.service.EmpleadoService;
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
     * Devuelve los datos del empleado autenticado.
     * Sirve para verificar usuario y contraseña desde el front.
     */
    @GetMapping("/me")
    public UsuarioActualDTO usuarioActual(Authentication authentication) {

        return empleadoRepository.findByUsuario(authentication.getName())
                .map(UsuarioActualDTO::desde)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuario no encontrado"
                ));
    }

    /**
     * Cambia la contraseña del usuario autenticado.
     * Requiere la contraseña actual.
     */
    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cambiarPassword(
            @RequestBody CambioPasswordDTO dto,
            Authentication authentication) {

        empleadoService.cambiarPasswordPropia(
                authentication.getName(),
                dto.getPasswordActual(),
                dto.getPasswordNueva()
        );
    }
}