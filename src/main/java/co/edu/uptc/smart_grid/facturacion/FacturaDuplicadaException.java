package co.edu.uptc.smart_grid.facturacion;

import co.edu.uptc.smart_grid.compartido.NegocioException;

public class FacturaDuplicadaException extends NegocioException {
    public FacturaDuplicadaException(String mensaje) { super(mensaje); }
}