package co.edu.uptc.usuarios_service.compartido;

public class NegocioException extends RuntimeException {
    private final int estadoHttp;

    public NegocioException(String mensaje, int estadoHttp) {
        super(mensaje);
        this.estadoHttp = estadoHttp;
    }

    public int getEstadoHttp() { return estadoHttp; }
}