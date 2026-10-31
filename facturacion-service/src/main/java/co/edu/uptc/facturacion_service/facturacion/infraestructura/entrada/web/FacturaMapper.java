package co.edu.uptc.facturacion_service.facturacion.infraestructura.entrada.web;

import co.edu.uptc.facturacion_service.facturacion.dominio.Factura;
import co.edu.uptc.facturacion_service.facturacion.dominio.TarifaVigente;

import java.time.LocalDate;

public class FacturaMapper {
    private FacturaMapper() {}

    public static TarifaVigente aTarifaVigente(FacturaRequest request) {
        return new TarifaVigente(request.tarifaValor(), request.tarifaVigenciaDesde(), request.tarifaVigenciaHasta());
    }

    public static FacturaResponse aResponse(Factura factura) {
        TarifaVigente tarifa = factura.getTarifaAplicada();
        return new FacturaResponse(factura.getId(), factura.getMedidorId(), factura.getPeriodoInicio(),
            factura.getPeriodoFin(), factura.getMontoTotal(), factura.estadoActual(LocalDate.now()),
            factura.getConsumoAcumuladoKwh(), tarifa.valor(), tarifa.vigenciaDesde(), tarifa.vigenciaHasta(),
            factura.getFechaGeneracion(), factura.getFechaVencimiento());
    }
}