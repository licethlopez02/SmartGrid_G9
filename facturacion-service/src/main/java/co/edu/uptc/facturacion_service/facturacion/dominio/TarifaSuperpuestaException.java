package co.edu.uptc.facturacion_service.facturacion.dominio;

import co.edu.uptc.facturacion_service.compartido.NegocioException;

public class TarifaSuperpuestaException extends NegocioException {
    public TarifaSuperpuestaException() {
        super("La vigencia de la tarifa se superpone con la de otra tarifa registrada.");
    }
}