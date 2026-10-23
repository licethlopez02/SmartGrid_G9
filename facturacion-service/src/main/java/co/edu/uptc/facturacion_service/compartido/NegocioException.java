package co.edu.uptc.facturacion_service.compartido;

public abstract class NegocioException extends RuntimeException {
    protected NegocioException(String mensaje) {
        super(mensaje);
    }
}