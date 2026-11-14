package co.edu.uptc.medicion_service.medicion.aplicacion.servicio;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.entrada.ConsultarMedicionUseCase;
import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.AnomaliaRepositoryPort;
import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.MedidorRepositoryPort;
import co.edu.uptc.medicion_service.medicion.dominio.DeteccionAnomaliaService;
import co.edu.uptc.medicion_service.medicion.dominio.LecturaConsumo;
import co.edu.uptc.medicion_service.medicion.dominio.Medidor;
import co.edu.uptc.medicion_service.medicion.dominio.MedidorFactory;

public final class ConsultarMedicionApplicationService implements ConsultarMedicionUseCase {

	private final MedidorRepositoryPort repository;
	private final DeteccionAnomaliaService deteccionAnomalia;
	private final AnomaliaRepositoryPort anomaliaRepository;
	private final Duration separacionMaxima;

	public ConsultarMedicionApplicationService(MedidorRepositoryPort repository) {
		this(repository, null, Duration.ofHours(24));
	}

	public ConsultarMedicionApplicationService(MedidorRepositoryPort repository,
			AnomaliaRepositoryPort anomaliaRepository) {
		this(repository, anomaliaRepository, Duration.ofHours(24));
	}

	public ConsultarMedicionApplicationService(MedidorRepositoryPort repository,
			AnomaliaRepositoryPort anomaliaRepository, Duration separacionMaxima) {
		this.repository = Objects.requireNonNull(repository, "El repositorio es obligatorio");
		this.deteccionAnomalia = new DeteccionAnomaliaService();
		this.anomaliaRepository = anomaliaRepository;
		this.separacionMaxima = Objects.requireNonNull(separacionMaxima, "La separación máxima es obligatoria");
		if (separacionMaxima.isZero() || separacionMaxima.isNegative()) {
			throw new IllegalArgumentException("La separación máxima debe ser positiva");
		}
	}

	@Override
	public Medidor buscarMedidor(Long medidorId) {
		return repository.buscarPorId(Objects.requireNonNull(medidorId, "El id del medidor es obligatorio"))
				.orElseThrow(() -> new IllegalArgumentException("El medidor no existe"));
	}

	@Override
	public Medidor buscarMedidorSinHistorial(Long medidorId) {
		return repository.buscarPorIdSinHistorial(Objects.requireNonNull(medidorId, "El id del medidor es obligatorio"))
				.orElseThrow(() -> new IllegalArgumentException("El medidor no existe"));
	}

	@Override
	public List<Medidor> listar() {
		return repository.listar();
	}

	@Override
	public List<LecturaConsumo> historial(Long medidorId, Instant desde, Instant hasta) {
		return filtrar(buscarMedidor(medidorId), desde, hasta);
	}

	@Override
	public BigDecimalResultado consumo(Long medidorId, Instant desde, Instant hasta) {
		List<LecturaConsumo> lecturas = historial(medidorId, desde, hasta);
		BigDecimal total = lecturas.stream().map(LecturaConsumo::valor)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		return new BigDecimalResultado(total, lecturas.size(), desde, hasta);
	}

	@Override
	public List<ResultadoLectura> anomalias(Long medidorId) {
		Medidor medidor = buscarMedidor(medidorId);
		var anteriores = MedidorFactory.crear(medidor.id(), medidor.ubicacion());
		return medidor.historialLecturas().stream().map(lectura -> {
			var resultado = deteccionAnomalia.detectar(anteriores, lectura);
			anteriores.registrarLecturaFueraDeOrden(lectura);
			return new ResultadoLectura(lectura, resultado);
		}).filter(resultado -> resultado.resultado().esAnomalia()).toList();
	}

	@Override
	public List<co.edu.uptc.medicion_service.medicion.dominio.Anomalia> anomaliasNoResueltas(
			Long medidorId, Instant desde, Instant hasta) {
		buscarMedidor(medidorId);
		if (anomaliaRepository == null) {
			return List.of();
		}
		return anomaliaRepository.buscarPorMedidor(medidorId, desde, hasta, true);
	}

	@Override
	public List<co.edu.uptc.medicion_service.medicion.dominio.Anomalia> anomaliasPersistidas(
			Long medidorId, Instant desde, Instant hasta) {
		buscarMedidor(medidorId);
		if (anomaliaRepository != null) {
			return anomaliaRepository.buscarPorMedidor(medidorId, desde, hasta, false);
		}
		return anomalias(medidorId).stream()
				.map(r -> new co.edu.uptc.medicion_service.medicion.dominio.Anomalia(null, medidorId,
						r.lectura().valor(), r.lectura().timestamp(), r.resultado().razon(), false))
				.toList();
	}

	@Override
	public boolean periodoCompleto(Long medidorId, Instant desde, Instant hasta) {
		if (desde == null || hasta == null || desde.isAfter(hasta)) {
			return false;
		}
		List<LecturaConsumo> lecturas = historial(medidorId, null, null);
		if (lecturas.isEmpty()) {
			return false;
		}
		if (lecturas.getFirst().timestamp().isAfter(desde)
				|| lecturas.getLast().timestamp().isBefore(hasta)) {
			return false;
		}
		Instant anterior = desde;
		for (LecturaConsumo lectura : lecturas) {
			if (lectura.timestamp().isBefore(desde) || lectura.timestamp().isAfter(hasta)) {
				continue;
			}
			if (Duration.between(anterior, lectura.timestamp()).compareTo(separacionMaxima) > 0) {
				return false;
			}
			anterior = lectura.timestamp();
		}
		return Duration.between(anterior, hasta).compareTo(separacionMaxima) <= 0;
	}

	@Override
	public List<ConsumoZona> consumoPorZona(String zona, Instant desde, Instant hasta) {
		if (zona == null || zona.isBlank()) {
			throw new IllegalArgumentException("La zona es obligatoria");
		}
		return repository.listar().stream()
				.filter(medidor -> zona.equalsIgnoreCase(medidor.ubicacion()))
				.collect(Collectors.groupingBy(Medidor::ubicacion, Collectors.reducing(
						BigDecimal.ZERO,
						medidor -> filtrar(medidor, desde, hasta).stream()
								.map(LecturaConsumo::valor).reduce(BigDecimal.ZERO, BigDecimal::add),
						BigDecimal::add)))
				.entrySet().stream()
				.map(entry -> new ConsumoZona(entry.getKey(), entry.getValue()))
				.toList();
	}

	private static List<LecturaConsumo> filtrar(Medidor medidor, Instant desde, Instant hasta) {
		if (desde != null && hasta != null && desde.isAfter(hasta)) {
			throw new IllegalArgumentException("El inicio no puede ser posterior al fin");
		}
		return medidor.historialLecturas().stream()
				.filter(lectura -> desde == null || !lectura.timestamp().isBefore(desde))
				.filter(lectura -> hasta == null || !lectura.timestamp().isAfter(hasta))
				.toList();
	}
}
