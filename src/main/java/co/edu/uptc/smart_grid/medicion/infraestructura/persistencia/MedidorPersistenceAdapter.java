package co.edu.uptc.smart_grid.medicion.infraestructura.persistencia;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uptc.smart_grid.medicion.aplicacion.puerto.salida.MedidorRepositoryPort;
import co.edu.uptc.smart_grid.medicion.dominio.LecturaConsumo;
import co.edu.uptc.smart_grid.medicion.dominio.Medidor;
import co.edu.uptc.smart_grid.medicion.dominio.MedidorFactory;

@Repository
public class MedidorPersistenceAdapter implements MedidorRepositoryPort {

	private final SpringDataMedidorRepository repository;

	public MedidorPersistenceAdapter(SpringDataMedidorRepository repository) {
		this.repository = Objects.requireNonNull(repository, "El repositorio Spring Data es obligatorio");
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Medidor> buscarPorId(Long medidorId) {
		return repository.findById(aDatabaseId(medidorId)).map(this::toDomain);
	}

	@Override
	public Medidor guardar(Medidor medidor) {
		Objects.requireNonNull(medidor, "El medidor es obligatorio");
		repository.save(toEntity(medidor));
		return medidor;
	}

	private Medidor toDomain(MedidorJpaEntity entity) {
		List<LecturaConsumo> lecturas = entity.getLecturas().stream()
				.map(lectura -> new LecturaConsumo(
						lectura.getValor(), lectura.getTimestamp(), aLongId(lectura.getMedidorId())))
				.toList();
		return MedidorFactory.crearConHistorial(aLongId(entity.getId()), entity.getUbicacion(), lecturas);
	}

	private MedidorJpaEntity toEntity(Medidor medidor) {
		List<LecturaJpaEmbeddable> lecturas = medidor.historialLecturas().stream()
				.map(lectura -> new LecturaJpaEmbeddable(
						lectura.valor(), lectura.timestamp(), aDatabaseId(lectura.medidorId())))
				.toList();
		return new MedidorJpaEntity(aDatabaseId(medidor.id()), medidor.ubicacion(), lecturas);
	}

	private static Long aLongId(String id) {
		String valor = id.startsWith("M-") ? id.substring(2) : id;
		return Long.valueOf(valor);
	}

	private static String aDatabaseId(Long id) {
		return id.toString();
	}
}
