package co.edu.uptc.medicion_service.medicion.infraestructura.salida.mensajeria;

import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.PublicadorEventos;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Component
public class OutboxPublicadorEventos implements PublicadorEventos {

    private final OutboxEventRepository repositorio;
    private final ObjectMapper objectMapper;

    public OutboxPublicadorEventos(OutboxEventRepository repositorio, ObjectMapper objectMapper) {
        this.repositorio = repositorio;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publicar(String topico, String clave, Map<String, Object> contenido) {
        String payload = objectMapper.writeValueAsString(contenido);
        repositorio.save(new OutboxEvent(topico, clave, payload));
    }
}