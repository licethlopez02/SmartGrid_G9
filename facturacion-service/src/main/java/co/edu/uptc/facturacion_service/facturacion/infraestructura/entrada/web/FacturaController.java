package co.edu.uptc.facturacion_service.facturacion.infraestructura.entrada.web;

import co.edu.uptc.facturacion_service.facturacion.aplicacion.AccesoFacturasService;
import co.edu.uptc.facturacion_service.facturacion.aplicacion.FacturacionMensualService;
import co.edu.uptc.facturacion_service.facturacion.aplicacion.FacturaUseCase;
import co.edu.uptc.facturacion_service.facturacion.aplicacion.FacturarPeriodoUseCase;
import co.edu.uptc.facturacion_service.facturacion.aplicacion.ResumenFacturacionMensual;
import co.edu.uptc.facturacion_service.facturacion.dominio.Factura;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    private final FacturaUseCase facturaUseCase;
    private final FacturarPeriodoUseCase facturarPeriodo;
    private final FacturacionMensualService facturacionMensual;
    private final AccesoFacturasService acceso;

    public FacturaController(FacturaUseCase facturaUseCase, FacturarPeriodoUseCase facturarPeriodo,
                             FacturacionMensualService facturacionMensual, AccesoFacturasService acceso) {
        this.facturaUseCase = facturaUseCase;
        this.facturarPeriodo = facturarPeriodo;
        this.facturacionMensual = facturacionMensual;
        this.acceso = acceso;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('OPERADOR')")
    public FacturaResponse generar(@RequestBody FacturaRequest request) {
        return FacturaMapper.aResponse(facturarPeriodo.generarFacturaDelPeriodo(
            request.medidorId(), request.periodoInicio(), request.periodoFin()));
    }

    @PostMapping("/mensual")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResumenFacturacionMensual facturarMes(@RequestParam String mes) {
        try {
            return facturacionMensual.facturarMes(YearMonth.parse(mes));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("El mes debe tener el formato AAAA-MM");
        }
    }

    @GetMapping("/mias")
    public List<FacturaResponse> mias(@AuthenticationPrincipal Jwt jwt) {
        List<Long> medidores = acceso.idsDeMedidoresDe(usuarioId(jwt));
        return facturaUseCase.listarPorMedidores(medidores).stream().map(FacturaMapper::aResponse).toList();
    }

    @GetMapping("/{id}")
    public FacturaResponse buscarPorId(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Factura factura = facturaUseCase.buscarPorId(id);
        acceso.verificarAcceso(usuarioId(jwt), esOperador(jwt), factura.getMedidorId());
        return FacturaMapper.aResponse(factura);
    }

    @GetMapping
    public List<FacturaResponse> listarPorMedidor(@RequestParam Long medidorId, @AuthenticationPrincipal Jwt jwt) {
        acceso.verificarAcceso(usuarioId(jwt), esOperador(jwt), medidorId);
        return facturaUseCase.listarPorMedidor(medidorId).stream().map(FacturaMapper::aResponse).toList();
    }

    @PostMapping("/{id}/pago")
    @PreAuthorize("hasRole('OPERADOR')")
    public FacturaResponse pagar(@PathVariable Long id) {
        return FacturaMapper.aResponse(facturaUseCase.registrarPago(id));
    }

    private static Long usuarioId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }

    private static boolean esOperador(Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList("roles");
        return roles != null && roles.contains("OPERADOR");
    }
}