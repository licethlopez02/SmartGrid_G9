package co.edu.uptc.medicion_service.medicion.aplicacion.servicio;

import java.util.Objects;
import java.util.HashMap;
import java.time.Instant;
import java.math.BigDecimal;

import org.springframework.transaction.annotation.Transactional;

import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.AnomaliaRepositoryPort;
import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.entrada.DetectarAnomaliaUseCase;
import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.MedidorRepositoryPort;
import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.PublicadorEventos;
import co.edu.uptc.medicion_service.medicion.dominio.Anomalia;
import co.edu.uptc.medicion_service.medicion.dominio.DeteccionAnomaliaService;
import co.edu.uptc.medicion_service.medicion.dominio.LecturaConsumo;
import co.edu.uptc.medicion_service.medicion.dominio.Medidor;
import co.edu.uptc.medicion_service.medicion.dominio.ResultadoDeteccion;

public class DetectarAnomaliaApplicationService implements DetectarAnomaliaUseCase {

	private final MedidorRepositoryPort medidorRepository;
	private final DeteccionAnomaliaService deteccionAnomalia;
	private final AnomaliaRepositoryPort anomaliaRepository;
	private final PublicadorEventos publicadorEventos;

	public DetectarAnomaliaApplicationService(MedidorRepositoryPort medidorRepository) {
		this(medidorRepository, new DeteccionAnomaliaService(), null, null);
	}

	public DetectarAnomaliaApplicationService(MedidorRepositoryPort medidorRepository,
			DeteccionAnomaliaService deteccionAnomalia) {
		this(medidorRepository, deteccionAnomalia, null, null);
	}

	public DetectarAnomaliaApplicationService(MedidorRepositoryPort medidorRepository,
			DeteccionAnomaliaService deteccionAnomalia,
			AnomaliaRepositoryPort anomaliaRepository,
			PublicadorEventos publicadorEventos) {
		this.medidorRepository = Objects.requireNonNull(medidorRepository, "El repositorio es obligatorio");
		this.deteccionAnomalia = Objects.requireNonNull(deteccionAnomalia,
				"El servicio de detección es obligatorio");
		this.anomaliaRepository = anomaliaRepository;
		this.publicadorEventos = publicadorEventos;
	}

	@Override
	@Transactional
	public ResultadoDeteccion detectar(Long medidorId, LecturaConsumo nuevaLectura) {
		Objects.requireNonNull(medidorId, "El id del medidor es obligatorio");
		Objects.requireNonNull(nuevaLectura, "La nueva lectura es obligatoria");
		Medidor medidor = medidorRepository.buscarPorId(medidorId)
				.orElseThrow(() -> new IllegalArgumentException("El medidor no existe"));
		ResultadoDeteccion resultado = deteccionAnomalia.detectar(medidor, nuevaLectura);
		medidor.registrarLecturaFueraDeOrden(nuevaLectura);
		medidorRepository.guardar(medidor);
		medidorRepository.guardarLectura(nuevaLectura);
		if (resultado.esAnomalia() && anomaliaRepository != null) {
			Anomalia anomalia = anomaliaRepository.guardar(new Anomalia(null, medidorId,
					nuevaLectura.valor(), nuevaLectura.timestamp(), resultado.razon(), false));
			publicarAlerta("anomalias.detectadas", medidor, anomalia, false);
		}
		return resultado;
	}

	@Override
	@Transactional
	public void alertarSensorDefectuoso(Long medidorId, BigDecimal valor, Instant timestamp, String razon) {
		Objects.requireNonNull(valor, "El valor es obligatorio");
		if (valor.signum() >= 0) {
			throw new IllegalArgumentException("La alerta de sensor defectuoso requiere una lectura negativa");
		}
		Medidor medidor = medidorRepository.buscarPorId(medidorId)
				.orElseThrow(() -> new IllegalArgumentException("El medidor no existe"));
		if (publicadorEventos != null) {
			publicarSensorDefectuoso(medidor, valor, timestamp, razon);
		}
	}

	@Override
	@Transactional
	public void resolver(Long anomaliaId) {
		if (anomaliaRepository == null) {
			throw new IllegalStateException("El repositorio de anomalías no está configurado");
		}
		Anomalia anomalia = anomaliaRepository.buscarPorId(anomaliaId)
				.orElseThrow(() -> new IllegalArgumentException("La anomalía no existe"));
		anomalia.resolver();
		anomaliaRepository.guardar(anomalia);
	}

	private void publicarSensorDefectuoso(Medidor medidor, BigDecimal valor, Instant timestamp, String razon) {
		HashMap<String, Object> contenido = base(medidor);
		contenido.put("valor", valor);
		contenido.put("timestamp", timestamp.toString());
		contenido.put("razon", razon);
		publicadorEventos.publicar("sensores.defectuosos", String.valueOf(medidor.id()), contenido);
	}

	private void publicarAlerta(String topico, Medidor medidor, Anomalia anomalia, boolean sensorDefectuoso) {
		HashMap<String, Object> contenido = base(medidor);
		contenido.put("anomaliaId", anomalia.id());
		contenido.put("valor", anomalia.valor());
		contenido.put("timestamp", anomalia.timestamp().toString());
		contenido.put("razon", anomalia.razon());
		contenido.put("resuelta", anomalia.resuelta());
		contenido.put("sensorDefectuoso", sensorDefectuoso);
		publicadorEventos.publicar(topico, String.valueOf(medidor.id()), contenido);
	}

	private static HashMap<String, Object> base(Medidor medidor) {
		HashMap<String, Object> contenido = new HashMap<>();
		contenido.put("medidorId", medidor.id());
		if (medidor.usuarioId() != null) {
			contenido.put("usuarioId", medidor.usuarioId());
			contenido.put("destinatarioId", medidor.usuarioId());
		}
		return contenido;
	}
}
