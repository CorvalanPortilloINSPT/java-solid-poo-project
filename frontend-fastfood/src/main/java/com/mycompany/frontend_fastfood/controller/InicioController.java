package com.mycompany.frontend_fastfood.controller;

import com.mycompany.frontend_fastfood.cliente.Sesion;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Menú principal después del login. Las opciones dependen del rol,
 * como los menús de la versión de consola (GerenteMenu, VendedorMenu,
 * CocineroMenu), más el del SYS_ADMIN.
 */
@Controller
public class InicioController {

    @GetMapping("/inicio")
    public String inicio(HttpSession sesion, Model model) {
        model.addAttribute("usuario", Sesion.usuario(sesion));
        return "inicio";
    }
}