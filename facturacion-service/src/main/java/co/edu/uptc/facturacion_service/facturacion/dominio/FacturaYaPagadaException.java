package co.edu.uptc.facturacion_service.facturacion.dominio;

import co.edu.uptc.facturacion_service.compartido.NegocioException;

public class FacturaYaPagadaException extends NegocioException {
    public FacturaYaPagadaException(Long facturaId) {
        super("La factura " + facturaId + " ya está pagada.");
    }
}