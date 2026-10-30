package co.edu.uptc.medicion_service.medicion.infraestructura.entrada.mensajeria;

import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.entrada.EliminarMedidorUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class FacturasMarcadasListener {

    private static final Logger log = LoggerFactory.getLogger(FacturasMarcadasListener.class);

    private final EliminarMedidorUseCase eliminarMedidor;
    private final ObjectMapper objectMapper;

    public FacturasMarcadasListener(EliminarMedidorUseCase eliminarMedidor, ObjectMapper objectMapper) {
        this.eliminarMedidor = eliminarMedidor;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "facturas.marcadas-huerfanas")
    public void alMarcarseFacturas(String mensaje) {
        long medidorId = objectMapper.readTree(mensaje).get("medidorId").longValue();
        log.info("Saga: facturacion confirmó para el medidor {}. Eliminando medidor.", medidorId);
        eliminarMedidor.confirmarEliminacion(medidorId);
    }
}