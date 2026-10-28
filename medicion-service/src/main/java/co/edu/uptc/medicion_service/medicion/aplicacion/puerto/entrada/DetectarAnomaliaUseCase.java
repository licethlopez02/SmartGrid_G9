package co.edu.uptc.medicion_service.medicion.aplicacion.puerto.entrada;

import co.edu.uptc.medicion_service.medicion.dominio.LecturaConsumo;
import co.edu.uptc.medicion_service.medicion.dominio.ResultadoDeteccion;

public interface DetectarAnomaliaUseCase {

	ResultadoDeteccion detectar(Long medidorId, LecturaConsumo nuevaLectura);
}
