package co.edu.uptc.facturacion_service.facturacion.infraestructura.entrada.web;

import co.edu.uptc.facturacion_service.facturacion.aplicacion.FacturaUseCase;
import co.edu.uptc.facturacion_service.facturacion.aplicacion.FacturarPeriodoUseCase;
import co.edu.uptc.facturacion_service.facturacion.dominio.Factura;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    private final FacturaUseCase facturaUseCase;
    private final FacturarPeriodoUseCase facturarPeriodo;

    public FacturaController(FacturaUseCase facturaUseCase, FacturarPeriodoUseCase facturarPeriodo) {
        this.facturaUseCase = facturaUseCase;
        this.facturarPeriodo = facturarPeriodo;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FacturaResponse generar(@RequestBody FacturaRequest request) {
        return FacturaMapper.aResponse(facturarPeriodo.generarFacturaDelPeriodo(
            request.medidorId(), request.periodoInicio(), request.periodoFin()));
    }

    @GetMapping("/{id}")
    public FacturaResponse buscarPorId(@PathVariable Long id) {
        return FacturaMapper.aResponse(facturaUseCase.buscarPorId(id));
    }

    @GetMapping
    public List<FacturaResponse> listarPorMedidor(@RequestParam Long medidorId) {
        return facturaUseCase.listarPorMedidor(medidorId).stream().map(FacturaMapper::aResponse).toList();
    }

    @PostMapping("/{id}/pago")
    public FacturaResponse pagar(@PathVariable Long id) {
        return FacturaMapper.aResponse(facturaUseCase.registrarPago(id));
    }
}
