package co.edu.uptc.usuarios_service.usuarios.aplicacion;

import co.edu.uptc.usuarios_service.usuarios.dominio.TipoRol;
import co.edu.uptc.usuarios_service.usuarios.dominio.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificacionAlertasServiceTest {

    private RepositorioUsuarios repositorio;
    private EnviadorCorreo correo;
    private NotificacionAlertasService servicio;

    @BeforeEach
    void preparar() {
        repositorio = mock(RepositorioUsuarios.class);
        correo = mock(EnviadorCorreo.class);
        servicio = new NotificacionAlertasService(repositorio, correo);
    }

    private Usuario usuario(boolean activo) {
        Usuario usuario = new Usuario("Ana", "ana@correo.com", "hash", Set.of(TipoRol.USUARIO));
        usuario.setId(7L);
        usuario.cambiarEstado(activo);
        return usuario;
    }

    @Test
    void enviaCorreoAlUsuarioActivo() {
        when(repositorio.buscarPorId(7L)).thenReturn(java.util.Optional.of(usuario(true)));

        servicio.notificarAnomalia("10", 7L, new BigDecimal("30"),
                Instant.parse("2026-11-13T10:00:00Z"), "Desviación");

        verify(correo).enviar(eq("ana@correo.com"), contains("anómalo"), contains("30"));
    }

    @Test
    void noEnviaAlUsuarioInactivo() {
        when(repositorio.buscarPorId(7L)).thenReturn(java.util.Optional.of(usuario(false)));

        servicio.notificarSensorDefectuoso("10", 7L, new BigDecimal("-5"),
                Instant.parse("2026-11-13T10:03:00Z"), "Negativa");

        verify(correo, never()).enviar(anyString(), anyString(), anyString());
    }

    @Test
    void noEnviaSiElUsuarioNoExiste() {
        when(repositorio.buscarPorId(99L)).thenReturn(java.util.Optional.empty());

        servicio.notificarAnomalia("10", 99L, BigDecimal.ONE, Instant.now(), "Desviación");

        verify(correo, never()).enviar(anyString(), anyString(), anyString());
    }

    @Test
    void unFalloDeCorreoNoSePropaga() {
        when(repositorio.buscarPorId(7L)).thenReturn(java.util.Optional.of(usuario(true)));
        doThrow(new RuntimeException("SMTP caído")).when(correo)
                .enviar(anyString(), anyString(), anyString());

        assertDoesNotThrow(() -> servicio.notificarAnomalia(
                "10", 7L, BigDecimal.TEN, Instant.now(), "Desviación"));
    }
}
