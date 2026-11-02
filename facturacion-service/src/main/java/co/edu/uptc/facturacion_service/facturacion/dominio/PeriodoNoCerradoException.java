package co.edu.uptc.facturacion_service.facturacion.dominio;

import co.edu.uptc.facturacion_service.compartido.NegocioException;

import java.time.LocalDate;

public class PeriodoNoCerradoException extends NegocioException {
    public PeriodoNoCerradoException(LocalDate periodoFin) {
        super("El período termina el " + periodoFin + " y aún no ha concluido: solo se factura un período cerrado.");
    }
}