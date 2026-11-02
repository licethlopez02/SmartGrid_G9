package co.edu.uptc.facturacion_service.facturacion.infraestructura.entrada.web;

import co.edu.uptc.facturacion_service.facturacion.dominio.Factura;
import co.edu.uptc.facturacion_service.facturacion.dominio.TarifaVigente;

import java.time.LocalDate;
import java.util.List;

public class FacturaMapper {
    private FacturaMapper() {}

    public static FacturaResponse aResponse(Factura factura) {
        TarifaVigente tarifa = factura.getTarifaAplicada();
        List<AnomaliaFacturadaResponse> anomalias = factura.getAnomaliasNoResueltas().stream()
            .map(a -> new AnomaliaFacturadaResponse(a.anomaliaId(), a.valor(), a.momento(), a.razon()))
            .toList();
        return new FacturaResponse(factura.getId(), factura.getMedidorId(), factura.getPeriodoInicio(),
            factura.getPeriodoFin(), factura.getMontoTotal(), factura.estadoActual(LocalDate.now()),
            factura.getConsumoAcumuladoKwh(), tarifa.valor(), tarifa.vigenciaDesde(), tarifa.vigenciaHasta(),
            factura.getFechaGeneracion(), factura.getFechaVencimiento(), factura.getFechaPago(), anomalias);
    }
}