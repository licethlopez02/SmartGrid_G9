package co.edu.uptc.smart_grid.medicion.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

class MedidorTest {

	@Test
	void registrarLecturaAgregaLecturaValidaAlHistorial() {
		Medidor medidor = MedidorFactory.crear(1L, "Casa 1");
		LecturaConsumo lectura = lectura(1L, "10", 0);

		medidor.registrarLectura(lectura);

		assertEquals(List.of(lectura), medidor.historialLecturas());
	}

	@Test
	void registrarLecturaRechazaMedidorIdDiferente() {
		Medidor medidor = MedidorFactory.crear(1L, "Casa 1");

		assertThrows(IllegalArgumentException.class,
				() -> medidor.registrarLectura(lectura(2L, "10", 0)));
	}

	@Test
	void registrarLecturaRechazaTimestampAnterior() {
		Medidor medidor = MedidorFactory.crear(1L, "Casa 1");
		medidor.registrarLectura(lectura(1L, "10", 10));

		assertThrows(IllegalArgumentException.class,
				() -> medidor.registrarLectura(lectura(1L, "12", 9)));
	}

	@Test
	void historialRecienteDevuelveLasUltimasLecturasEnOrden() {
		Medidor medidor = MedidorFactory.crear(1L, "Casa 1");
		LecturaConsumo primera = lectura(1L, "10", 0);
		LecturaConsumo segunda = lectura(1L, "12", 1);
		LecturaConsumo tercera = lectura(1L, "14", 2);
		medidor.registrarLectura(primera);
		medidor.registrarLectura(segunda);
		medidor.registrarLectura(tercera);

		assertEquals(List.of(segunda, tercera), medidor.historialReciente(2));
	}

	@Test
	void historialRecienteRechazaCantidadMenorQueUno() {
		Medidor medidor = MedidorFactory.crear(1L, "Casa 1");

		assertThrows(IllegalArgumentException.class, () -> medidor.historialReciente(0));
		assertThrows(IllegalArgumentException.class, () -> medidor.historialReciente(-1));
	}

	private static LecturaConsumo lectura(Long medidorId, String valor, long segundo) {
		return new LecturaConsumo(
				new BigDecimal(valor), Instant.parse("2026-01-01T10:00:00Z").plusSeconds(segundo), medidorId);
	}
}
