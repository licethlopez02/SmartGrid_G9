package co.edu.uptc.facturacion_service.facturacion.dominio;

import co.edu.uptc.facturacion_service.compartido.NegocioException;

public class AccesoDenegadoException extends NegocioException {
    public AccesoDenegadoException() {
        super("No tiene acceso a las facturas de este medidor.");
    }
}
