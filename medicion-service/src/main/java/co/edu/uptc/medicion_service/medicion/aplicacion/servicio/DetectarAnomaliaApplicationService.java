package co.edu.uptc.medicion_service.medicion.aplicacion.servicio;

import java.util.Objects;

import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.entrada.DetectarAnomaliaUseCase;
import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.MedidorRepositoryPort;
import co.edu.uptc.medicion_service.medicion.dominio.DeteccionAnomaliaService;
import co.edu.uptc.medicion_service.medicion.dominio.LecturaConsumo;
import co.edu.uptc.medicion_service.medicion.dominio.Medidor;
import co.edu.uptc.medicion_service.medicion.dominio.ResultadoDeteccion;

public final class DetectarAnomaliaApplicationService implements DetectarAnomaliaUseCase {

	private final MedidorRepositoryPort medidorRepository;
	private final DeteccionAnomaliaService deteccionAnomalia;

	public DetectarAnomaliaApplicationService(MedidorRepositoryPort medidorRepository) {
		this(medidorRepository, new DeteccionAnomaliaService());
	}

	public DetectarAnomaliaApplicationService(MedidorRepositoryPort medidorRepository,
			DeteccionAnomaliaService deteccionAnomalia) {
		this.medidorRepository = Objects.requireNonNull(medidorRepository, "El repositorio es obligatorio");
		this.deteccionAnomalia = Objects.requireNonNull(deteccionAnomalia,
				"El servicio de detección es obligatorio");
	}

	@Override
	public ResultadoDeteccion detectar(Long medidorId, LecturaConsumo nuevaLectura) {
		Objects.requireNonNull(medidorId, "El id del medidor es obligatorio");
		Objects.requireNonNull(nuevaLectura, "La nueva lectura es obligatoria");
		Medidor medidor = medidorRepository.buscarPorId(medidorId)
				.orElseThrow(() -> new IllegalArgumentException("El medidor no existe"));
		ResultadoDeteccion resultado = deteccionAnomalia.detectar(medidor, nuevaLectura);
		medidor.registrarLectura(nuevaLectura);
		medidorRepository.guardar(medidor);
		return resultado;
	}
}
