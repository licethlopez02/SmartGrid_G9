package co.edu.uptc.medicion_service.medicion.aplicacion.puerto.entrada;

import java.math.BigDecimal;
import java.time.Instant;

import co.edu.uptc.medicion_service.medicion.dominio.LecturaConsumo;
import co.edu.uptc.medicion_service.medicion.dominio.ResultadoDeteccion;

public interface DetectarAnomaliaUseCase {

	ResultadoDeteccion detectar(Long medidorId, LecturaConsumo nuevaLectura);

	default void alertarSensorDefectuoso(Long medidorId, BigDecimal valor, Instant timestamp, String razon) {
		throw new UnsupportedOperationException();
	}

	default void resolver(Long anomaliaId) {
		throw new UnsupportedOperationException();
	}
}
