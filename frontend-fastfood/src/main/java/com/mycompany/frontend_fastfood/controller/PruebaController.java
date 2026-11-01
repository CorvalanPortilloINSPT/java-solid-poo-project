package com.mycompany.frontend_fastfood.controller;

import com.mycompany.frontend_fastfood.cliente.BackendClient;
import com.mycompany.frontend_fastfood.cliente.BackendException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Página de prueba: verifica los JSP y el BackendClient
 * (respuesta correcta, error traducido y backend apagado).
 */
@Controller
public class PruebaController {

    private final BackendClient backendClient;

    public PruebaController(BackendClient backendClient) {
        this.backendClient = backendClient;
    }

    @GetMapping("/prueba")
    public String prueba(Model model) {

        // 1. Endpoint público: tiene que responder el texto del backend.
        try {
            String respuesta = backendClient.get("/api/prueba", String.class, null);
            model.addAttribute("conectado", true);
            model.addAttribute("mensaje", respuesta);
        } catch (BackendException e) {
            model.addAttribute("conectado", false);
            model.addAttribute("mensaje", e.getMessage());
        }

        // 2. Endpoint protegido sin credenciales: tiene que dar 401 traducido.
        try {
            backendClient.get("/api/auth/me", String.class, null);
            model.addAttribute("errorEsperado", "No dio error (algo está mal: debería dar 401)");
        } catch (BackendException e) {
            model.addAttribute("errorEsperado",
                    "Código " + e.getStatus() + ": " + e.getMessage());
        }

        return "prueba";
    }
}