package co.edu.uptc.smart_grid.facturacion;

import co.edu.uptc.smart_grid.compartido.NegocioException;

public class PeriodoIncompletoException extends NegocioException {
    public PeriodoIncompletoException(String mensaje) { super(mensaje); }
}