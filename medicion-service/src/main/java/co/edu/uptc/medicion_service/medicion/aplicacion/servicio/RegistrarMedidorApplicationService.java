package co.edu.uptc.medicion_service.medicion.aplicacion.servicio;

import java.util.Objects;

import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.entrada.RegistrarMedidorUseCase;
import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.MedidorRepositoryPort;
import co.edu.uptc.medicion_service.medicion.dominio.Medidor;
import co.edu.uptc.medicion_service.medicion.dominio.MedidorFactory;

public final class RegistrarMedidorApplicationService implements RegistrarMedidorUseCase {

	private final MedidorRepositoryPort medidorRepository;

	public RegistrarMedidorApplicationService(MedidorRepositoryPort medidorRepository) {
		this.medidorRepository = Objects.requireNonNull(medidorRepository, "El repositorio es obligatorio");
	}

	@Override
	public Medidor registrar(Long id, String ubicacion) {
		Objects.requireNonNull(id, "El id del medidor es obligatorio");
		if (medidorRepository.buscarPorId(id).isPresent()) {
			throw new IllegalArgumentException("Ya existe un medidor con id " + id);
		}
		Medidor medidor = MedidorFactory.crear(id, ubicacion);
		return medidorRepository.guardar(medidor);
	}
}
