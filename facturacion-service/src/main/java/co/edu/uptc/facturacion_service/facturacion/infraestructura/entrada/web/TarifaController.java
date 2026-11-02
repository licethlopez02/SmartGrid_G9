package co.edu.uptc.facturacion_service.facturacion.infraestructura.entrada.web;

import co.edu.uptc.facturacion_service.facturacion.aplicacion.TarifaUseCase;
import co.edu.uptc.facturacion_service.facturacion.dominio.Tarifa;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tarifas")
public class TarifaController {

    private final TarifaUseCase tarifaUseCase;

    public TarifaController(TarifaUseCase tarifaUseCase) {
        this.tarifaUseCase = tarifaUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('OPERADOR')")
    public TarifaResponse registrar(@RequestBody TarifaRequest request) {
        return aResponse(tarifaUseCase.registrar(request.valor(), request.vigenciaDesde(), request.vigenciaHasta()));
    }

    @GetMapping
    public List<TarifaResponse> listar() {
        return tarifaUseCase.listar().stream().map(TarifaController::aResponse).toList();
    }

    private static TarifaResponse aResponse(Tarifa tarifa) {
        return new TarifaResponse(tarifa.getId(), tarifa.vigente().valor(),
                tarifa.vigente().vigenciaDesde(), tarifa.vigente().vigenciaHasta());
    }
}