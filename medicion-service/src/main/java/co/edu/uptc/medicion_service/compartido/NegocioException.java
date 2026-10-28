package co.edu.uptc.medicion_service.compartido;

public abstract class NegocioException extends RuntimeException {
    protected NegocioException(String mensaje) {
        super(mensaje);
    }
}