package co.edu.uptc.smart_grid.compartido;

public abstract class NegocioException extends RuntimeException {
    protected NegocioException(String mensaje) {
        super(mensaje);
    }
}