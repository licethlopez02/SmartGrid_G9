package co.edu.uptc.usuarios_service.usuarios.aplicacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import co.edu.uptc.usuarios_service.usuarios.dominio.CorreoYaRegistradoException;
import co.edu.uptc.usuarios_service.usuarios.dominio.CredencialesInvalidasException;
import co.edu.uptc.usuarios_service.usuarios.dominio.TipoRol;
import co.edu.uptc.usuarios_service.usuarios.dominio.Usuario;

public class AutenticacionServiceTest {
    private RepositorioUsuariosFalso repositorio;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private AutenticacionService servicio;

    @BeforeEach
    void preparar() {
        repositorio = new RepositorioUsuariosFalso();
        servicio = new AutenticacionService(repositorio, encoder,
                u -> new TokenAcceso("token-de-" + u.getCorreo(), 3600));
    }

    @Test
    void registraConRolUsuarioYContrasenaHasheada() {
        Usuario u = servicio.registrar("Ana", "Ana@Correo.com", "clave12345");
        assertNotNull(u.getId());
        assertEquals("ana@correo.com", u.getCorreo());
        assertEquals(Set.of(TipoRol.USUARIO), u.getRoles());
        assertNotEquals("clave12345", u.getContrasenaHash());
        assertTrue(encoder.matches("clave12345", u.getContrasenaHash()));
    }

    @Test
    void rechazaCorreoDuplicado() {
        servicio.registrar("Ana", "ana@correo.com", "clave12345");
        assertThrows(CorreoYaRegistradoException.class,
                () -> servicio.registrar("Otra Ana", "ANA@correo.com", "otraClave99"));
    }

    @Test
    void loginCorrectoDevuelveToken() {
        servicio.registrar("Ana", "ana@correo.com", "clave12345");
        TokenAcceso t = servicio.iniciarSesion("ana@correo.com", "clave12345");
        assertEquals("token-de-ana@correo.com", t.token());
    }

    @Test
    void loginRechazaContrasenaIncorrecta() {
        servicio.registrar("Ana", "ana@correo.com", "clave12345");
        assertThrows(CredencialesInvalidasException.class,
                () -> servicio.iniciarSesion("ana@correo.com", "otraClave99"));
    }

    @Test
    void loginRechazaUsuarioInactivo() {
        Usuario u = servicio.registrar("Ana", "ana@correo.com", "clave12345");
        u.cambiarEstado(false);
        repositorio.guardar(u);
        assertThrows(CredencialesInvalidasException.class,
                () -> servicio.iniciarSesion("ana@correo.com", "clave12345"));
    }
}