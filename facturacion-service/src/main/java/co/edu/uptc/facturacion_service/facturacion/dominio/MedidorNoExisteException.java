package co.edu.uptc.facturacion_service.facturacion.dominio;

import co.edu.uptc.facturacion_service.compartido.NegocioException;

public class MedidorNoExisteException extends NegocioException {
    public MedidorNoExisteException(Long medidorId) {
        super("No existe un medidor con id " + medidorId + " en medicion-service.");
    }
}