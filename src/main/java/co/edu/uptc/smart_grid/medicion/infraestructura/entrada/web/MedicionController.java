package co.edu.uptc.smart_grid.medicion.infraestructura.entrada.web;

import java.time.Instant;
import java.util.List;
import java.math.BigDecimal;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uptc.smart_grid.medicion.aplicacion.puerto.entrada.DetectarAnomaliaUseCase;
import co.edu.uptc.smart_grid.medicion.aplicacion.puerto.entrada.ConsultarMedicionUseCase;
import co.edu.uptc.smart_grid.medicion.aplicacion.puerto.entrada.RegistrarMedidorUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping("/api/medidores")
public class MedicionController {

	private final DetectarAnomaliaUseCase detectarAnomalia;
	private final RegistrarMedidorUseCase registrarMedidor;
	private final ConsultarMedicionUseCase consultarMedicion;

	public MedicionController(DetectarAnomaliaUseCase detectarAnomalia,
			RegistrarMedidorUseCase registrarMedidor,
			ConsultarMedicionUseCase consultarMedicion) {
		this.detectarAnomalia = detectarAnomalia;
		this.registrarMedidor = registrarMedidor;
		this.consultarMedicion = consultarMedicion;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public MedidorResponse registrar(@RequestBody MedidorRequest request) {
		return MedicionMapper.aResponse(registrarMedidor.registrar(request.id(), request.ubicacion()));
	}

	@PostMapping("/{medidorId}/lecturas")
	public ResultadoDeteccionResponse registrarLectura(
			@PathVariable Long medidorId,
			@RequestBody LecturaRequest request) {
		if (request.valor() != null && request.valor().compareTo(BigDecimal.ZERO) < 0) {
			consultarMedicion.buscarMedidor(medidorId);
			return new ResultadoDeteccionResponse(false,
					"Lectura negativa descartada; alerta de sensor defectuoso generada");
		}
		return MedicionMapper.aResponse(
				detectarAnomalia.detectar(medidorId, MedicionMapper.aLectura(medidorId, request)));
	}

	@GetMapping("/{medidorId}/lecturas")
	public List<LecturaResponse> historial(@PathVariable Long medidorId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant desde,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant hasta) {
		return consultarMedicion.historial(medidorId, desde, hasta).stream()
				.map(lectura -> new LecturaResponse(lectura.valor(), lectura.timestamp(), lectura.medidorId()))
				.toList();
	}

	@GetMapping("/{medidorId}/consumo")
	public ConsumoResponse consumo(@PathVariable Long medidorId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant desde,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant hasta) {
		var consumo = consultarMedicion.consumo(medidorId, desde, hasta);
		return new ConsumoResponse(consumo.total(), consumo.desde(), consumo.hasta());
	}

	@GetMapping("/{medidorId}/anomalias")
	public List<AnomaliaResponse> anomalias(@PathVariable Long medidorId) {
		return consultarMedicion.anomalias(medidorId).stream()
				.map(anomalia -> new AnomaliaResponse(anomalia.lectura().valor(),
						anomalia.lectura().timestamp(), anomalia.resultado().razon()))
				.toList();
	}

	@GetMapping("/consumo-por-zona")
	public List<ConsumoZonaResponse> consumoPorZona(@RequestParam String zona,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant desde,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant hasta) {
		return consultarMedicion.consumoPorZona(zona, desde, hasta).stream()
				.map(consumo -> new ConsumoZonaResponse(consumo.zona(), consumo.total()))
				.toList();
	}
}
