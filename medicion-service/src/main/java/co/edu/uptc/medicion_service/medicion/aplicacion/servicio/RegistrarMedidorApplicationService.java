package co.edu.uptc.medicion_service.medicion.aplicacion.servicio;

import java.util.Map;
import java.util.Objects;

import org.springframework.transaction.annotation.Transactional;

import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.entrada.RegistrarMedidorUseCase;
import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.MedidorRepositoryPort;
import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.PublicadorEventos;
import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.VerificadorUsuario;
import co.edu.uptc.medicion_service.medicion.dominio.Medidor;
import co.edu.uptc.medicion_service.medicion.dominio.MedidorFactory;

public class RegistrarMedidorApplicationService implements RegistrarMedidorUseCase {

	private final MedidorRepositoryPort medidorRepository;
	private final PublicadorEventos publicadorEventos;
	private final VerificadorUsuario verificadorUsuario;

	public RegistrarMedidorApplicationService(MedidorRepositoryPort medidorRepository,
			PublicadorEventos publicadorEventos) {
		this(medidorRepository, publicadorEventos, usuarioId -> true);
	}

	public RegistrarMedidorApplicationService(MedidorRepositoryPort medidorRepository,
			PublicadorEventos publicadorEventos, VerificadorUsuario verificadorUsuario) {
		this.medidorRepository = Objects.requireNonNull(medidorRepository, "El repositorio es obligatorio");
		this.publicadorEventos = Objects.requireNonNull(publicadorEventos, "El publicador es obligatorio");
		this.verificadorUsuario = Objects.requireNonNull(verificadorUsuario, "El verificador de usuario es obligatorio");
	}

	@Override
	@Transactional
	public Medidor registrar(Long id, String ubicacion) {
		return registrar(id, ubicacion, null, ubicacion);
	}

	@Override
	@Transactional
	public Medidor registrar(Long id, String ubicacion, Long usuarioId, String propiedad) {
		Objects.requireNonNull(id, "El id del medidor es obligatorio");
		if (usuarioId != null && !verificadorUsuario.existeYEstaActivo(usuarioId)) {
			throw new IllegalArgumentException("El usuario no existe o está inactivo");
		}
		if (medidorRepository.buscarPorId(id).isPresent()) {
			throw new IllegalArgumentException("Ya existe un medidor con id " + id);
		}
		String propiedadFinal = propiedad == null || propiedad.isBlank() ? ubicacion : propiedad;
		if (usuarioId != null && medidorRepository.existeActivoPorUsuarioYPropiedad(usuarioId, propiedadFinal)) {
			throw new IllegalArgumentException("La propiedad ya tiene un medidor asociado a un usuario activo");
		}
		Medidor medidor = MedidorFactory.crear(id, ubicacion, usuarioId, propiedadFinal);
		Medidor guardado = medidorRepository.guardar(medidor);

		// Transactional Outbox: el evento se guarda en la MISMA transacción que el medidor.
		publicadorEventos.publicar(
				"medidores.registrados",
				String.valueOf(id),
				usuarioId == null
						? Map.of("id", id, "ubicacion", ubicacion, "propiedad", propiedadFinal)
						: Map.of("id", id, "ubicacion", ubicacion, "propiedad", propiedadFinal,
								"usuarioId", usuarioId));
		return guardado;
	}
}