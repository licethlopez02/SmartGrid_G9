package co.edu.uptc.usuarios_service.usuarios.dominio;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class UsuarioTest {
    @Test
    void unUsuarioPuedeTenerAmbosRoles() {
        Usuario u = new Usuario("Ana", "ana@correo.com", "hash", EnumSet.of(TipoRol.USUARIO));
        u.reemplazarRoles(EnumSet.of(TipoRol.USUARIO, TipoRol.OPERADOR));
        assertTrue(u.tieneRol(TipoRol.USUARIO));
        assertTrue(u.tieneRol(TipoRol.OPERADOR));
    }

    @Test
    void noPermiteReemplazarRolesPorVacio() {
        Usuario u = new Usuario("Ana", "ana@correo.com", "hash", EnumSet.of(TipoRol.USUARIO));
        assertThrows(ReglaDeUsuarioException.class, () -> u.reemplazarRoles(Set.of()));
    }

    @Test
    void noPermiteCrearUsuarioSinRoles() {
        assertThrows(ReglaDeUsuarioException.class,
                () -> new Usuario("Ana", "ana@correo.com", "hash", Set.of()));
    }
}