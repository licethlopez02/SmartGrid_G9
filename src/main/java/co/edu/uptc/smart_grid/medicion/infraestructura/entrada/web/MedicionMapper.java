package co.edu.uptc.smart_grid.medicion.infraestructura.entrada.web;

import java.util.Objects;

import co.edu.uptc.smart_grid.medicion.dominio.LecturaConsumo;
import co.edu.uptc.smart_grid.medicion.dominio.Medidor;
import co.edu.uptc.smart_grid.medicion.dominio.ResultadoDeteccion;

public final class MedicionMapper {

	private MedicionMapper() {
	}

	public static LecturaConsumo aLectura(Long medidorId, LecturaRequest request) {
		Objects.requireNonNull(request, "La lectura es obligatoria");
		return new LecturaConsumo(request.valor(), request.timestamp(), medidorId);
	}

	public static ResultadoDeteccionResponse aResponse(ResultadoDeteccion resultado) {
		Objects.requireNonNull(resultado, "El resultado es obligatorio");
		return new ResultadoDeteccionResponse(resultado.esAnomalia(), resultado.razon());
	}

	public static MedidorResponse aResponse(Medidor medidor) {
		Objects.requireNonNull(medidor, "El medidor es obligatorio");
		return new MedidorResponse(medidor.id(), medidor.ubicacion());
	}
}
