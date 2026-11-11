package co.edu.uptc.medicion_service.medicion.infraestructura.entrada.web;

import java.time.Instant;
import java.util.List;
import java.math.BigDecimal;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.entrada.DetectarAnomaliaUseCase;
import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.entrada.EliminarMedidorUseCase;
import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.entrada.ConsultarMedicionUseCase;
import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.entrada.RegistrarMedidorUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.AccessDeniedException;
import java.util.Objects;

@RestController
@RequestMapping("/api/medidores")
public class MedicionController {

	private final DetectarAnomaliaUseCase detectarAnomalia;
	private final RegistrarMedidorUseCase registrarMedidor;
	private final ConsultarMedicionUseCase consultarMedicion;
	private final EliminarMedidorUseCase eliminarMedidor;

	public MedicionController(DetectarAnomaliaUseCase detectarAnomalia,
			RegistrarMedidorUseCase registrarMedidor,
			ConsultarMedicionUseCase consultarMedicion,
			EliminarMedidorUseCase eliminarMedidor) {
		this.detectarAnomalia = detectarAnomalia;
		this.registrarMedidor = registrarMedidor;
		this.consultarMedicion = consultarMedicion;
		this.eliminarMedidor = eliminarMedidor;
	}

	@PostMapping
	@PreAuthorize("hasRole('OPERADOR')")
	@ResponseStatus(HttpStatus.CREATED)
	public MedidorResponse registrar(@RequestBody MedidorRequest request) {
		return MedicionMapper.aResponse(registrarMedidor.registrar(request.id(), request.ubicacion(),
				request.usuarioId(), request.propiedad()));
	}

	@GetMapping("/mios")
	public List<MedidorResponse> misMedidores(@AuthenticationPrincipal Jwt jwt) {
		if (jwt == null) {
			throw new AccessDeniedException("Se requiere autenticación");
		}
		Long usuarioId;
		try {
			usuarioId = Long.valueOf(jwt.getSubject());
		} catch (RuntimeException e) {
			throw new AccessDeniedException("El token no contiene un usuario válido");
		}
		return consultarMedicion.listar().stream()
				.filter(medidor -> Objects.equals(usuarioId, medidor.usuarioId()))
				.map(MedicionMapper::aResponse)
				.toList();
	}

	@PostMapping("/{medidorId}/lecturas")
	public ResultadoDeteccionResponse registrarLectura(
			@PathVariable Long medidorId,
			@RequestBody LecturaRequest request,
			@AuthenticationPrincipal Jwt jwt) {
		verificarAcceso(medidorId, jwt);
		if (request.valor() != null && request.valor().compareTo(BigDecimal.ZERO) < 0) {
			consultarMedicion.buscarMedidor(medidorId);
			detectarAnomalia.alertarSensorDefectuoso(medidorId, request.valor(),
					request.timestamp() == null ? java.time.Instant.now() : request.timestamp(),
					"Se recibió una lectura negativa");
			return new ResultadoDeteccionResponse(false,
					"Lectura negativa descartada; alerta de sensor defectuoso generada");
		}
		return MedicionMapper.aResponse(
				detectarAnomalia.detectar(medidorId, MedicionMapper.aLectura(medidorId, request)));
	}

	@GetMapping("/{medidorId}/lecturas")
	public List<LecturaResponse> historial(@PathVariable Long medidorId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant desde,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant hasta,
			@AuthenticationPrincipal Jwt jwt) {
		verificarAcceso(medidorId, jwt);
		return consultarMedicion.historial(medidorId, desde, hasta).stream()
				.map(lectura -> new LecturaResponse(lectura.valor(), lectura.timestamp(), lectura.medidorId()))
				.toList();
	}

	@GetMapping("/{medidorId}/consumo")
	public ConsumoResponse consumo(@PathVariable Long medidorId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant desde,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant hasta,
			@AuthenticationPrincipal Jwt jwt) {
		verificarAcceso(medidorId, jwt);
		var consumo = consultarMedicion.consumo(medidorId, desde, hasta);
		return new ConsumoResponse(consumo.total(), consumo.desde(), consumo.hasta());
	}

	@GetMapping("/{medidorId}/anomalias")
	public List<AnomaliaResponse> anomalias(@PathVariable Long medidorId,
			@AuthenticationPrincipal Jwt jwt) {
		verificarAcceso(medidorId, jwt);
		return consultarMedicion.anomaliasPersistidas(medidorId, null, null).stream()
				.map(anomalia -> new AnomaliaResponse(anomalia.id(), anomalia.valor(),
						anomalia.timestamp(), anomalia.razon(), anomalia.resuelta()))
				.toList();
	}

	@GetMapping("/{medidorId}/anomalias/pendientes")
	public List<AnomaliaResponse> anomaliasNoResueltas(@PathVariable Long medidorId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant desde,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant hasta,
			@AuthenticationPrincipal Jwt jwt) {
		verificarAcceso(medidorId, jwt);
		return consultarMedicion.anomaliasNoResueltas(medidorId, desde, hasta).stream()
				.map(a -> new AnomaliaResponse(a.id(), a.valor(), a.timestamp(), a.razon(), a.resuelta()))
				.toList();
	}

	@PostMapping("/{medidorId}/anomalias/{anomaliaId}/resolver")
	public ResponseEntity<Void> resolverAnomalia(@PathVariable Long medidorId,
			@PathVariable Long anomaliaId, @AuthenticationPrincipal Jwt jwt) {
		verificarAcceso(medidorId, jwt);
		boolean pertenece = consultarMedicion.anomaliasPersistidas(medidorId, null, null).stream()
				.anyMatch(a -> Objects.equals(a.id(), anomaliaId));
		if (!pertenece) {
			throw new IllegalArgumentException("La anomalía no pertenece al medidor");
		}
		detectarAnomalia.resolver(anomaliaId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/consumo-por-zona")
	@PreAuthorize("hasRole('OPERADOR')")
	public List<ConsumoZonaResponse> consumoPorZona(@RequestParam String zona,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant desde,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant hasta) {
		return consultarMedicion.consumoPorZona(zona, desde, hasta).stream()
				.map(consumo -> new ConsumoZonaResponse(consumo.zona(), consumo.total()))
				.toList();
	}

	@DeleteMapping("/{medidorId}")
	@PreAuthorize("hasRole('OPERADOR')")
	public ResponseEntity<Void> eliminar(@PathVariable Long medidorId) {
		eliminarMedidor.iniciarEliminacion(medidorId);
		return ResponseEntity.accepted().build();
	}

	private void verificarAcceso(Long medidorId, @AuthenticationPrincipal Jwt jwt) {
		MedidorResponse medidor = MedicionMapper.aResponse(consultarMedicion.buscarMedidor(medidorId));
		if (jwt == null || tieneRol(jwt, "OPERADOR")) {
			return;
		}
		Long usuarioId;
		try {
			usuarioId = Long.valueOf(jwt.getSubject());
		} catch (RuntimeException e) {
			throw new AccessDeniedException("El token no contiene un usuario válido");
		}
		if (!Objects.equals(usuarioId, medidor.usuarioId())) {
			throw new AccessDeniedException("El medidor no pertenece al usuario autenticado");
		}
	}

	private static boolean tieneRol(Jwt jwt, String rol) {
		Object roles = jwt.getClaims().get("roles");
		return roles instanceof java.util.Collection<?> collection
				&& collection.stream().map(String::valueOf).anyMatch(rol::equals);
	}
}
