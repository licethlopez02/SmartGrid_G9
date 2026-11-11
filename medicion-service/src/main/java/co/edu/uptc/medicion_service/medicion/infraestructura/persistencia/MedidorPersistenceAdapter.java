package co.edu.uptc.medicion_service.medicion.infraestructura.persistencia;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.MedidorRepositoryPort;
import co.edu.uptc.medicion_service.medicion.dominio.LecturaConsumo;
import co.edu.uptc.medicion_service.medicion.dominio.Medidor;
import co.edu.uptc.medicion_service.medicion.dominio.MedidorFactory;

@Repository
public class MedidorPersistenceAdapter implements MedidorRepositoryPort {

	private final SpringDataMedidorRepository repository;
	private final SpringDataLecturaRepository lecturasRepository;

	public MedidorPersistenceAdapter(SpringDataMedidorRepository repository,
			SpringDataLecturaRepository lecturasRepository) {
		this.repository = Objects.requireNonNull(repository, "El repositorio Spring Data es obligatorio");
		this.lecturasRepository = Objects.requireNonNull(lecturasRepository, "El repositorio de lecturas es obligatorio");
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Medidor> buscarPorId(Long medidorId) {
		return repository.findById(aDatabaseId(medidorId)).map(this::toDomain);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Medidor> listar() {
		return repository.findAll().stream().map(this::toDomain).toList();
	}

	@Override
	public Medidor guardar(Medidor medidor) {
		Objects.requireNonNull(medidor, "El medidor es obligatorio");
		repository.save(toEntity(medidor));
		return medidor;
	}

	@Override
	@Transactional
	public void guardarLectura(LecturaConsumo lectura) {
		Objects.requireNonNull(lectura, "La lectura es obligatoria");
		lecturasRepository.save(new LecturaJpaEntity(aDatabaseId(lectura.medidorId()),
				lectura.valor(), lectura.timestamp()));
	}

	private Medidor toDomain(MedidorJpaEntity entity) {
		List<LecturaConsumo> lecturas = lecturasRepository.findByMedidorIdOrderByTimestampAsc(entity.getId()).stream()
				.map(lectura -> new LecturaConsumo(
						lectura.getValor(), lectura.getTimestamp(), aLongId(lectura.getMedidorId())))
				.toList();
		Medidor medidor = MedidorFactory.crearConHistorial(aLongId(entity.getId()), entity.getUbicacion(),
				entity.getUsuarioId(), entity.getPropiedad(), lecturas);
		if ("EN_ELIMINACION".equals(entity.getEstado())) {
			medidor.marcarEnEliminacion();
		}
		return medidor;
	}

	private MedidorJpaEntity toEntity(Medidor medidor) {
		return new MedidorJpaEntity(aDatabaseId(medidor.id()), medidor.ubicacion(), medidor.usuarioId(),
				medidor.propiedad(),
				medidor.estado().name());
	}

	@Override
	@Transactional
	public void eliminar(Long medidorId) {
		repository.deleteById(aDatabaseId(medidorId));
	}
	
	private static Long aLongId(String id) {
		String valor = id.startsWith("M-") ? id.substring(2) : id;
		return Long.valueOf(valor);
	}

	private static String aDatabaseId(Long id) {
		return id.toString();
	}
}
