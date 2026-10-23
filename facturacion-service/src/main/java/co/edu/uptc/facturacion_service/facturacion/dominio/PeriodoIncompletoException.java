package co.edu.uptc.facturacion_service.facturacion.dominio;

import co.edu.uptc.facturacion_service.compartido.NegocioException;

public class PeriodoIncompletoException extends NegocioException {
    public PeriodoIncompletoException(String mensaje) { 
        super(mensaje); 
    }
}