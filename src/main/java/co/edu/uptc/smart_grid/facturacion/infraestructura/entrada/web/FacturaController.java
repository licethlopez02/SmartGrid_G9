package co.edu.uptc.smart_grid.facturacion.infraestructura.entrada.web;

import co.edu.uptc.smart_grid.facturacion.aplicacion.FacturaUseCase;
import co.edu.uptc.smart_grid.facturacion.dominio.Factura;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    private final FacturaUseCase facturaUseCase;

    public FacturaController(FacturaUseCase facturaUseCase) {
        this.facturaUseCase = facturaUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FacturaResponse generar(@RequestBody FacturaRequest request) {
        Factura factura = facturaUseCase.generarFactura(request.medidorId(), request.periodoInicio(),
            request.periodoFin(), request.consumoAcumuladoKwh(), request.periodoCompleto(),
            FacturaMapper.aTarifaVigente(request));
        return FacturaMapper.aResponse(factura);
    }

    @GetMapping("/{id}")
    public FacturaResponse buscarPorId(@PathVariable Long id) {
        return FacturaMapper.aResponse(facturaUseCase.buscarPorId(id));
    }

    @GetMapping
    public List<FacturaResponse> listarPorMedidor(@RequestParam Long medidorId) {
        return facturaUseCase.listarPorMedidor(medidorId).stream().map(FacturaMapper::aResponse).toList();
    }
}
