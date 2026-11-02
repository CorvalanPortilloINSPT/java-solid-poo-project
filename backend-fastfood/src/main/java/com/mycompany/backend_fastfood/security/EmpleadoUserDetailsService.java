package com.mycompany.backend_fastfood.security;

import com.mycompany.backend_fastfood.model.Empleado;
import com.mycompany.backend_fastfood.repository.EmpleadoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Conecta Spring Security con los empleados guardados en MySQL.
 *
 * Reglas tomadas del sistema anterior (EmpleadoDAO.login):
 * - el usuario tiene que existir;
 * - el empleado tiene que estar activo;
 * - la contraseña tiene que coincidir.
 *
 * El rol del empleado se traduce a la autoridad ROLE_<ROL>
 * (por ejemplo ROLE_GERENTE), que es lo que usa SecurityConfig.
 */
@Slf4j
@Service
public class EmpleadoUserDetailsService implements UserDetailsService {

    private final EmpleadoRepository empleadoRepository;

    public EmpleadoUserDetailsService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usuario)
            throws UsernameNotFoundException {

        Empleado empleado = empleadoRepository.findByUsuario(usuario)
                .orElseThrow(() -> {
                    log.warn("Intento de login con usuario inexistente: {}", usuario);
                    return new UsernameNotFoundException(
                            "Usuario o contraseña incorrectos");
                });

        if (!empleado.isActivo()) {
            log.warn("Intento de login de un empleado dado de baja: {}", usuario);
        }

        User.UserBuilder builder = User.withUsername(empleado.getUsuario())
                .password(empleado.getContraseña())
                .disabled(!empleado.isActivo());

        if (empleado.getRol() != null) {
            builder.roles(empleado.getRol().name());
        } else {
            log.warn("El empleado {} no tiene rol asignado", usuario);
            builder.authorities(new String[0]);
        }

        return builder.build();
    }
}