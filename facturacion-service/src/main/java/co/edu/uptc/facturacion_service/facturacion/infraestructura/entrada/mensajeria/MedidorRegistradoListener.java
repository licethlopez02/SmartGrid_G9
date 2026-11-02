package co.edu.uptc.facturacion_service.facturacion.infraestructura.entrada.mensajeria;

import co.edu.uptc.facturacion_service.facturacion.aplicacion.SincronizacionMedidoresService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class MedidorRegistradoListener {

    private static final Logger log = LoggerFactory.getLogger(MedidorRegistradoListener.class);

    private final SincronizacionMedidoresService sincronizacion;
    private final ObjectMapper objectMapper;

    public MedidorRegistradoListener(SincronizacionMedidoresService sincronizacion, ObjectMapper objectMapper) {
        this.sincronizacion = sincronizacion;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "medidores.registrados")
    public void alRegistrarseMedidor(String mensaje) {
        log.info("Evento recibido [medidores.registrados]: {}", mensaje);
        JsonNode json = objectMapper.readTree(mensaje);
        JsonNode usuario = json.get("usuarioId");
        Long usuarioId = (usuario == null || usuario.isNull()) ? null : usuario.longValue();
        sincronizacion.registrar(json.get("id").longValue(), usuarioId);
    }
}