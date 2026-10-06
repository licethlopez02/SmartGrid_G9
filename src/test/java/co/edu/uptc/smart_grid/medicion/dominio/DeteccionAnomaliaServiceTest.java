package co.edu.uptc.smart_grid.medicion.dominio;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

class DeteccionAnomaliaServiceTest {

	@Test
	void historialVacioNoEsAnomaliaYLaRazonLoIndica() {
		Medidor medidor = MedidorFactory.crear(1L, "Casa 1");

		ResultadoDeteccion resultado = new DeteccionAnomaliaService()
				.detectar(medidor, lectura(1L, "10", 0));

		assertFalse(resultado.esAnomalia());
		assertTrue(resultado.razon().contains("histórico"));
	}

	@Test
	void lecturaDentroDelUmbralNoEsAnomalia() {
		Medidor medidor = medidorConHistorial("10", "12");

		ResultadoDeteccion resultado = new DeteccionAnomaliaService()
				.detectar(medidor, lectura(1L, "15", 2));

		assertFalse(resultado.esAnomalia());
	}

	@Test
	void lecturaQueSuperaElUmbralEsAnomalia() {
		Medidor medidor = medidorConHistorial("10", "12");

		ResultadoDeteccion resultado = new DeteccionAnomaliaService()
				.detectar(medidor, lectura(1L, "30", 2));

		assertTrue(resultado.esAnomalia());
	}

	@Test
	void promedioHistoricoEnCeroYLecturaMayorQueCeroEsAnomalia() {
		Medidor medidor = medidorConHistorial("0", "0");

		ResultadoDeteccion resultado = new DeteccionAnomaliaService()
				.detectar(medidor, lectura(1L, "1", 2));

		assertTrue(resultado.esAnomalia());
	}

	@Test
	void rechazaLecturaDeOtroMedidor() {
		Medidor medidor = MedidorFactory.crear(1L, "Casa 1");

		assertThrows(IllegalArgumentException.class, () -> new DeteccionAnomaliaService()
				.detectar(medidor, lectura(2L, "10", 0)));
	}

	@Test
	void aceptaUmbralPersonalizado() {
		Medidor medidor = medidorConHistorial("10", "10");

		ResultadoDeteccion resultado = new DeteccionAnomaliaService(new BigDecimal("0.10"))
				.detectar(medidor, lectura(1L, "12", 2));

		assertTrue(resultado.esAnomalia());
	}

	@Test
	void rechazaUmbralNegativo() {
		assertThrows(IllegalArgumentException.class,
				() -> new DeteccionAnomaliaService(new BigDecimal("-0.01")));
	}

	private static Medidor medidorConHistorial(String primera, String segunda) {
		return MedidorFactory.crearConHistorial(1L, "Casa 1",
				List.of(lectura(1L, primera, 0), lectura(1L, segunda, 1)));
	}

	private static LecturaConsumo lectura(Long medidorId, String valor, long segundo) {
		return new LecturaConsumo(
				new BigDecimal(valor), Instant.parse("2026-01-01T10:00:00Z").plusSeconds(segundo), medidorId);
	}
}
