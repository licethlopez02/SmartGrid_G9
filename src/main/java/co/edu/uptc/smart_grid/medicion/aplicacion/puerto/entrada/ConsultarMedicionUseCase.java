package co.edu.uptc.smart_grid.medicion.aplicacion.puerto.entrada;

import java.time.Instant;
import java.util.List;

import co.edu.uptc.smart_grid.medicion.dominio.LecturaConsumo;
import co.edu.uptc.smart_grid.medicion.dominio.Medidor;
import co.edu.uptc.smart_grid.medicion.dominio.ResultadoDeteccion;

public interface ConsultarMedicionUseCase {

	Medidor buscarMedidor(Long medidorId);

	List<LecturaConsumo> historial(Long medidorId, Instant desde, Instant hasta);

	BigDecimalResultado consumo(Long medidorId, Instant desde, Instant hasta);

	List<ResultadoLectura> anomalias(Long medidorId);

	List<ConsumoZona> consumoPorZona(String zona, Instant desde, Instant hasta);

	record BigDecimalResultado(java.math.BigDecimal total, Instant desde, Instant hasta) {
	}

	record ResultadoLectura(LecturaConsumo lectura, ResultadoDeteccion resultado) {
	}

	record ConsumoZona(String zona, java.math.BigDecimal total) {
	}
}
