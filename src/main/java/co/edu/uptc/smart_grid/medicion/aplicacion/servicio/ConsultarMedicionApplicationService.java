package co.edu.uptc.smart_grid.medicion.aplicacion.servicio;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import co.edu.uptc.smart_grid.medicion.aplicacion.puerto.entrada.ConsultarMedicionUseCase;
import co.edu.uptc.smart_grid.medicion.aplicacion.puerto.salida.MedidorRepositoryPort;
import co.edu.uptc.smart_grid.medicion.dominio.DeteccionAnomaliaService;
import co.edu.uptc.smart_grid.medicion.dominio.LecturaConsumo;
import co.edu.uptc.smart_grid.medicion.dominio.Medidor;
import co.edu.uptc.smart_grid.medicion.dominio.MedidorFactory;

public final class ConsultarMedicionApplicationService implements ConsultarMedicionUseCase {

	private final MedidorRepositoryPort repository;
	private final DeteccionAnomaliaService deteccionAnomalia;

	public ConsultarMedicionApplicationService(MedidorRepositoryPort repository) {
		this.repository = Objects.requireNonNull(repository, "El repositorio es obligatorio");
		this.deteccionAnomalia = new DeteccionAnomaliaService();
	}

	@Override
	public Medidor buscarMedidor(Long medidorId) {
		return repository.buscarPorId(Objects.requireNonNull(medidorId, "El id del medidor es obligatorio"))
				.orElseThrow(() -> new IllegalArgumentException("El medidor no existe"));
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
		return new BigDecimalResultado(total, desde, hasta);
	}

	@Override
	public List<ResultadoLectura> anomalias(Long medidorId) {
		Medidor medidor = buscarMedidor(medidorId);
		var anteriores = MedidorFactory.crear(medidor.id(), medidor.ubicacion());
		return medidor.historialLecturas().stream().map(lectura -> {
			var resultado = deteccionAnomalia.detectar(anteriores, lectura);
			anteriores.registrarLectura(lectura);
			return new ResultadoLectura(lectura, resultado);
		}).filter(resultado -> resultado.resultado().esAnomalia()).toList();
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
