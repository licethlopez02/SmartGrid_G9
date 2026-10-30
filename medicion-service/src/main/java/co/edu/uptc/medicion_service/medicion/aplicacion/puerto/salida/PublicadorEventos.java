package co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida;

import java.util.Map;

public interface PublicadorEventos {

    void publicar(String topico, String clave, Map<String, Object> contenido);
}