package co.edu.uptc.facturacion_service.facturacion.dominio;

import co.edu.uptc.facturacion_service.compartido.NegocioException;

import java.time.LocalDate;

public class TarifaNoVigenteException extends NegocioException {
    public TarifaNoVigenteException(LocalDate fecha) {
        super("No hay una tarifa vigente para el " + fecha + ".");
    }
}
