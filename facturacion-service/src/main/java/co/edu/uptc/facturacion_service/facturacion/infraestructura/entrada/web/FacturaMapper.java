package co.edu.uptc.facturacion_service.facturacion.infraestructura.entrada.web;

import co.edu.uptc.facturacion_service.facturacion.dominio.Factura;
import co.edu.uptc.facturacion_service.facturacion.dominio.TarifaVigente;

public class FacturaMapper {
    private FacturaMapper() {}

    public static TarifaVigente aTarifaVigente(FacturaRequest request) {
        return new TarifaVigente(request.tarifaValor(), request.tarifaVigenciaDesde(), request.tarifaVigenciaHasta());
    }

    public static FacturaResponse aResponse(Factura factura) {
        return new FacturaResponse(factura.getId(), factura.getMedidorId(), factura.getPeriodoInicio(),
            factura.getPeriodoFin(), factura.getMontoTotal(), factura.getEstado());
    }
}