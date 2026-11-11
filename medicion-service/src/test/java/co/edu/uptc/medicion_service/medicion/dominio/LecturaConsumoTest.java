package co.edu.uptc.medicion_service.medicion.dominio;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Test;

class LecturaConsumoTest {

	@Test
	void creaLecturaValidaSinLanzarExcepcion() {
		assertDoesNotThrow(() -> new LecturaConsumo(
				new BigDecimal("12.50"), Instant.parse("2026-01-01T10:00:00Z"), 1L));
	}

	@Test
	void rechazaValorNegativo() {
		assertThrows(IllegalArgumentException.class, () -> new LecturaConsumo(
				new BigDecimal("-0.01"), Instant.parse("2026-01-01T10:00:00Z"), 1L));
	}

	@Test
	void rechazaMedidorIdNulo() {
		assertThrows(NullPointerException.class, () -> new LecturaConsumo(
				new BigDecimal("1"), Instant.parse("2026-01-01T10:00:00Z"), null));
	}

}
