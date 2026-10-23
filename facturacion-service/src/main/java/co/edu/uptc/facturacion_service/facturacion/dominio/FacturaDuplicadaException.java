package co.edu.uptc.facturacion_service.facturacion.dominio;

import co.edu.uptc.facturacion_service.compartido.NegocioException;

public class FacturaDuplicadaException extends NegocioException {
    public FacturaDuplicadaException(String mensaje) { super(mensaje); }
}