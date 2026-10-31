package co.edu.uptc.medicion_service.medicion.infraestructura.entrada.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.entrada.DetectarAnomaliaUseCase;

/** Short resource URL for clients that already know the anomaly id. */
@RestController
@RequestMapping("/api/anomalias")
public class AnomaliaController {

	private final DetectarAnomaliaUseCase detectarAnomalia;

	public AnomaliaController(DetectarAnomaliaUseCase detectarAnomalia) {
		this.detectarAnomalia = detectarAnomalia;
	}

	@PostMapping("/{anomaliaId}/resolver")
	@PreAuthorize("hasRole('OPERADOR')")
	public ResponseEntity<Void> resolver(@PathVariable Long anomaliaId) {
		detectarAnomalia.resolver(anomaliaId);
		return ResponseEntity.noContent().build();
	}
}
