package co.edu.uptc.facturacion_service.facturacion.infraestructura.entrada.mensajeria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MedidorRegistradoListener {

    private static final Logger log = LoggerFactory.getLogger(MedidorRegistradoListener.class);

    @KafkaListener(topics = "medidores.registrados")
    public void alRegistrarseMedidor(String mensaje) {
        log.info("Evento recibido [medidores.registrados]: {}", mensaje);
    }
}