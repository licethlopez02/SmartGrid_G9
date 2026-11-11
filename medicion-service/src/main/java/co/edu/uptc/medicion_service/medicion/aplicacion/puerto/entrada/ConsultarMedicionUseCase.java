package co.edu.uptc.medicion_service.medicion.aplicacion.puerto.entrada;

import java.time.Instant;
import java.util.List;

import co.edu.uptc.medicion_service.medicion.dominio.LecturaConsumo;
import co.edu.uptc.medicion_service.medicion.dominio.Medidor;
import co.edu.uptc.medicion_service.medicion.dominio.Anomalia;
import co.edu.uptc.medicion_service.medicion.dominio.ResultadoDeteccion;

public interface ConsultarMedicionUseCase {

	Medidor buscarMedidor(Long medidorId);

	List<Medidor> listar();

	List<LecturaConsumo> historial(Long medidorId, Instant desde, Instant hasta);

	BigDecimalResultado consumo(Long medidorId, Instant desde, Instant hasta);

	List<ResultadoLectura> anomalias(Long medidorId);

	List<Anomalia> anomaliasNoResueltas(Long medidorId, Instant desde, Instant hasta);

	List<Anomalia> anomaliasPersistidas(Long medidorId, Instant desde, Instant hasta);

	boolean periodoCompleto(Long medidorId, Instant desde, Instant hasta);

	List<ConsumoZona> consumoPorZona(String zona, Instant desde, Instant hasta);

	record BigDecimalResultado(java.math.BigDecimal total, int cantidadLecturas, Instant desde, Instant hasta) {
	}

	record ResultadoLectura(LecturaConsumo lectura, ResultadoDeteccion resultado) {
	}

	record ConsumoZona(String zona, java.math.BigDecimal total) {
	}
}
