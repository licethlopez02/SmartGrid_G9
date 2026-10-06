package co.edu.uptc.smart_grid.medicion.infraestructura.entrada.web;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uptc.smart_grid.medicion.aplicacion.puerto.entrada.DetectarAnomaliaUseCase;
import co.edu.uptc.smart_grid.medicion.aplicacion.puerto.entrada.RegistrarMedidorUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping("/api/medidores")
public class MedicionController {

	private final DetectarAnomaliaUseCase detectarAnomalia;
	private final RegistrarMedidorUseCase registrarMedidor;

	public MedicionController(DetectarAnomaliaUseCase detectarAnomalia,
			RegistrarMedidorUseCase registrarMedidor) {
		this.detectarAnomalia = detectarAnomalia;
		this.registrarMedidor = registrarMedidor;
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
		return MedicionMapper.aResponse(
				detectarAnomalia.detectar(medidorId, MedicionMapper.aLectura(medidorId, request)));
	}
}
