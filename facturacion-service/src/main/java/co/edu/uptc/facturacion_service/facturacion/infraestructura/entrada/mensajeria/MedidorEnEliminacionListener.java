package co.edu.uptc.facturacion_service.facturacion.infraestructura.entrada.mensajeria;

import co.edu.uptc.facturacion_service.facturacion.dominio.Factura;
import co.edu.uptc.facturacion_service.facturacion.infraestructura.salida.persistencia.FacturaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Component
public class MedidorEnEliminacionListener {

    private static final Logger log = LoggerFactory.getLogger(MedidorEnEliminacionListener.class);

    private final FacturaRepository facturaRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public MedidorEnEliminacionListener(FacturaRepository facturaRepository,
                                        KafkaTemplate<String, String> kafkaTemplate,
                                        ObjectMapper objectMapper) {
        this.facturaRepository = facturaRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "medidores.en-eliminacion")
    public void alIniciarseEliminacion(String mensaje) {
        long medidorId = objectMapper.readTree(mensaje).get("medidorId").longValue();

        List<Factura> facturas = facturaRepository.findByMedidorId(medidorId);
        facturas.forEach(Factura::marcarComoHuerfana);
        facturaRepository.saveAll(facturas);
        log.info("Saga: {} facturas del medidor {} marcadas como huérfanas", facturas.size(), medidorId);

        String confirmacion = objectMapper.writeValueAsString(
                Map.of("medidorId", medidorId, "cantidad", facturas.size()));
        kafkaTemplate.send("facturas.marcadas-huerfanas", String.valueOf(medidorId), confirmacion);
    }
}