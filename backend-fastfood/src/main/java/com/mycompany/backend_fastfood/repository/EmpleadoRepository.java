package com.mycompany.backend_fastfood.repository;

import com.mycompany.backend_fastfood.model.Empleado;
import com.mycompany.backend_fastfood.model.Rol;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadoRepository extends JpaRepository<Empleado, String> {

    /** Lo usa Spring Security para autenticar. */
    Optional<Empleado> findByUsuario(String usuario);

    /** DNI único (también contra empleados dados de baja). */
    boolean existsByDNI(String dni);

    /** Usuario único al registrar (también contra dados de baja). */
    boolean existsByUsuario(String usuario);

    /** Usuario único al modificar (excluye al propio empleado). */
    boolean existsByUsuarioAndIDNot(String usuario, String id);

    /**
     * Listado según quién consulta:
     * el gerente ve VENDEDOR y COCINERO; el SYS_ADMIN ve GERENTE y SYS_ADMIN.
     */
    List<Empleado> findByRolIn(Collection<?extends Rol> roles);

    /**
     * Protección de bajas: cuántos empleados activos quedan con un rol.
     * Sirve para no dar de baja al último GERENTE o SYS_ADMIN.
     */
    long countByRolAndActivoTrue(Rol rol);
}