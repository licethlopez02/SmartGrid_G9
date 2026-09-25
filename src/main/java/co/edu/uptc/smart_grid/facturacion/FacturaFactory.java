package co.edu.uptc.smart_grid.facturacion;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class FacturaFactory {

    private final FacturaRepository facturaRepository;
    private final GeneracionFacturaService generacionFacturaService;

    public FacturaFactory(FacturaRepository facturaRepository,
                           GeneracionFacturaService generacionFacturaService) {
        this.facturaRepository = facturaRepository;
        this.generacionFacturaService = generacionFacturaService;
    }

    public Factura crear(Long medidorId, LocalDate periodoInicio, LocalDate periodoFin,
                          BigDecimal consumoAcumuladoKwh, boolean periodoCompleto,
                          TarifaVigente tarifaVigente) {

        if (facturaRepository.existsByMedidorIdAndPeriodoInicioAndPeriodoFin(medidorId, periodoInicio, periodoFin)) {
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