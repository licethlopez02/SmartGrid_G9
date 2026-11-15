package co.edu.uptc.usuarios_service.usuarios.infraestructura.entrada.mensajeria;

import co.edu.uptc.usuarios_service.usuarios.aplicacion.NotificacionAlertasService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;

@Component
public class AlertasConsumoListener {

    private static final Logger log = LoggerFactory.getLogger(AlertasConsumoListener.class);

    private final ObjectMapper objectMapper;
    private final NotificacionAlertasService notificaciones;

    public AlertasConsumoListener(ObjectMapper objectMapper, NotificacionAlertasService notificaciones) {
        this.objectMapper = objectMapper;
        this.notificaciones = notificaciones;
    }

    @KafkaListener(topics = "anomalias.detectadas")
    public void alAnomaliaDetectada(String mensaje) {
        try {
            JsonNode json = objectMapper.readTree(mensaje);
            log.info("Evento recibido [anomalias.detectadas]: {}", mensaje);
            notificaciones.notificarAnomalia(
                    requerido(json, "medidorId").asText(),
                    destinatario(json),
                    decimal(json, "valor"),
                    Instant.parse(requerido(json, "timestamp").asText()),
                    texto(json, "razon"));
        } catch (Exception e) {
            log.error("Evento anomalias.detectadas inválido, se ignora: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "sensores.defectuosos")
    public void alSensorDefectuoso(String mensaje) {
        try {
            JsonNode json = objectMapper.readTree(mensaje);
            log.info("Evento recibido [sensores.defectuosos]: {}", mensaje);
            notificaciones.notificarSensorDefectuoso(
                    requerido(json, "medidorId").asText(),
                    destinatario(json),
                    decimal(json, "valor"),
                    Instant.parse(requerido(json, "timestamp").asText()),
                    texto(json, "razon"));
        } catch (Exception e) {
            log.error("Evento sensores.defectuosos inválido, se ignora: {}", e.getMessage());
        }
    }

    private Long destinatario(JsonNode json) {
        JsonNode destinatario = json.get("destinatarioId");
        if (destinatario != null && !destinatario.isNull()) {
            return destinatario.asLong();
        }
        JsonNode usuario = json.get("usuarioId");
        return usuario == null || usuario.isNull() ? null : usuario.asLong();
    }

    private String texto(JsonNode json, String campo) {
        JsonNode nodo = json.get(campo);
        return nodo == null || nodo.isNull() ? "sin detalle" : nodo.asText();
    }

    private JsonNode requerido(JsonNode json, String campo) {
        JsonNode nodo = json.get(campo);
        if (nodo == null || nodo.isNull()) {
            throw new IllegalArgumentException("Falta el campo " + campo);
        }
        return nodo;
    }

    private BigDecimal decimal(JsonNode json, String campo) {
        return requerido(json, campo).decimalValue();
    }
}
