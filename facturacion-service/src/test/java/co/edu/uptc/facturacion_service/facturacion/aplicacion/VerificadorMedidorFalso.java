package co.edu.uptc.facturacion_service.facturacion.aplicacion;

public class VerificadorMedidorFalso implements VerificadorMedidor {
    @Override
    public boolean existe(Long medidorId) {
        return true;
    }
}