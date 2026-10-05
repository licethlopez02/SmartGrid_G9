package co.edu.uptc.smart_grid.facturacion.aplicacion;

import org.springframework.stereotype.Component;

import co.edu.uptc.smart_grid.facturacion.dominio.Factura;
import co.edu.uptc.smart_grid.facturacion.dominio.FacturaDuplicadaException;
import co.edu.uptc.smart_grid.facturacion.dominio.PeriodoIncompletoException;
import co.edu.uptc.smart_grid.facturacion.dominio.TarifaVigente;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class FacturaFactory {

    private final RepositorioFacturas repositorioFacturas;
    private final GeneracionFacturaService generacionFacturaService;

    public FacturaFactory(RepositorioFacturas repositorioFacturas,
                           GeneracionFacturaService generacionFacturaService) {
        this.repositorioFacturas = repositorioFacturas;
        this.generacionFacturaService = generacionFacturaService;
    }

    public Factura crear(Long medidorId, LocalDate periodoInicio, LocalDate periodoFin,
                          BigDecimal consumoAcumuladoKwh, boolean periodoCompleto,
                          TarifaVigente tarifaVigente) {

        if (repositorioFacturas.existePorMedidorYPeriodo(medidorId, periodoInicio, periodoFin)) {
            throw new FacturaDuplicadaException(
                "Ya existe una factura generada para el medidor " + medidorId + " en ese período.");
        }

        boolean puedeGenerarse = generacionFacturaService.puedeGenerarse(periodoCompleto, tarifaVigente, periodoFin);
        if (!puedeGenerarse) {
            throw new PeriodoIncompletoException(
                "No se puede generar la factura del medidor " + medidorId +
                ": el período está incompleto o la tarifa no está vigente.");
        }

        return new Factura(medidorId, periodoInicio, periodoFin, consumoAcumuladoKwh,
            tarifaVigente, LocalDate.now());
    }
}