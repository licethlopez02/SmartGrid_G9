package co.edu.uptc.usuarios_service.usuarios.aplicacion;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import co.edu.uptc.usuarios_service.usuarios.dominio.ReglaDeUsuarioException;
import co.edu.uptc.usuarios_service.usuarios.dominio.TipoRol;
import co.edu.uptc.usuarios_service.usuarios.dominio.Usuario;

public class GestionUsuariosServiceTest {
    private RepositorioUsuariosFalso repositorio;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private GestionUsuariosService servicio;
    private Usuario ana;

    @BeforeEach
    void preparar() {
        repositorio = new RepositorioUsuariosFalso();
        servicio = new GestionUsuariosService(repositorio, encoder);
        ana = repositorio.guardar(new Usuario("Ana", "ana@correo.com",
                encoder.encode("clave12345"), EnumSet.of(TipoRol.USUARIO)));
    }

    @Test
    void rechazaContrasenaActualIncorrecta() {
        assertThrows(ReglaDeUsuarioException.class,
                () -> servicio.cambiarContrasena(ana.getId(), "incorrecta", "nuevaClave99"));
    }

    @Test
    void cambiaLaContrasena() {
        servicio.cambiarContrasena(ana.getId(), "clave12345", "nuevaClave99");
        Usuario guardada = repositorio.buscarPorId(ana.getId()).orElseThrow();
        assertTrue(encoder.matches("nuevaClave99", guardada.getContrasenaHash()));
    }

    @Test
    void unUsuarioPuedeQuedarConAmbosRoles() {
        Usuario u = servicio.reemplazarRoles(ana.getId(), EnumSet.of(TipoRol.USUARIO, TipoRol.OPERADOR));
        assertTrue(u.tieneRol(TipoRol.USUARIO));
        assertTrue(u.tieneRol(TipoRol.OPERADOR));
    }
}