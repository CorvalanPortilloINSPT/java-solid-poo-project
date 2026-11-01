package com.mycompany.backend_fastfood.config;

import com.mycompany.backend_fastfood.service.EmpleadoService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Se ejecuta una vez al arrancar la aplicación.
 *
 * Garantiza que siempre haya alguien que pueda administrar el sistema:
 * - si no hay ningún SYS_ADMIN activo, crea sysadmin / sysadmin123;
 * - si no hay ningún GERENTE activo, crea admin / admin123.
 * (Ver EmpleadoService.crearUsuariosInicialesSiFaltan.)
 *
 * Reemplaza a crearGerenteInicial() del sistema anterior, que creaba
 * un GERENTE y se llamaba en cada login.
 */
@Component
public class InicializadorDatos implements ApplicationRunner {

    private final EmpleadoService empleadoService;

    public InicializadorDatos(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @Override
    public void run(ApplicationArguments args) {
        empleadoService.crearUsuariosInicialesSiFaltan();
    }
}