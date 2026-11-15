package co.edu.uptc.usuarios_service.usuarios.aplicacion;

import co.edu.uptc.usuarios_service.usuarios.dominio.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class NotificacionAlertasService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionAlertasService.class);
    private static final DateTimeFormatter FORMATO = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm")
            .withZone(ZoneId.of("America/Bogota"));

    private final RepositorioUsuarios repositorioUsuarios;
    private final EnviadorCorreo enviadorCorreo;

    public NotificacionAlertasService(RepositorioUsuarios repositorioUsuarios,
                                      EnviadorCorreo enviadorCorreo) {
        this.repositorioUsuarios = repositorioUsuarios;
        this.enviadorCorreo = enviadorCorreo;
    }

    public void notificarAnomalia(String medidorId, Long destinatarioId, BigDecimal valor,
                                  Instant momento, String razon) {
        String asunto = "Smart Grid: alerta de consumo anómalo en tu medidor " + medidorId;
        String texto = "Se detectó una lectura anómala en tu medidor " + medidorId + ".\n\n"
                + "Valor leído: " + valor + " kWh\n"
                + "Fecha: " + FORMATO.format(momento) + " (hora de Bogotá)\n"
                + "Motivo: " + razon + "\n\n"
                + "Ingresa a Smart Grid, sección Alertas, para revisarla y marcarla como resuelta.";
        enviar(destinatarioId, asunto, texto);
    }

    public void notificarSensorDefectuoso(String medidorId, Long destinatarioId, BigDecimal valor,
                                          Instant momento, String razon) {
        String asunto = "Smart Grid: posible sensor defectuoso en tu medidor " + medidorId;
        String texto = "Tu medidor " + medidorId + " reportó una lectura inválida y fue descartada.\n\n"
                + "Valor reportado: " + valor + " kWh\n"
                + "Fecha: " + FORMATO.format(momento) + " (hora de Bogotá)\n"
                + "Motivo: " + razon + "\n\n"
                + "Es posible que el sensor esté fallando. Contacta al operador de la red.";
        enviar(destinatarioId, asunto, texto);
    }

    private void enviar(Long destinatarioId, String asunto, String texto) {
        if (destinatarioId == null) {
            log.warn("Alerta sin destinatario: no se envía correo");
            return;
        }
        Usuario usuario = repositorioUsuarios.buscarPorId(destinatarioId).orElse(null);
        if (usuario == null || !usuario.isActivo()) {
            log.warn("Usuario {} no existe o está inactivo: no se envía correo", destinatarioId);
            return;
        }
        try {
            enviadorCorreo.enviar(usuario.getCorreo(), asunto, texto);
            log.info("Alerta enviada al usuario {}", destinatarioId);
        } catch (RuntimeException e) {
            log.error("No se pudo enviar la alerta al usuario {}: {}", destinatarioId, e.getMessage());
        }
    }
}
