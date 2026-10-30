package co.edu.uptc.facturacion_service.facturacion.infraestructura.salida.grpc;

import co.edu.uptc.facturacion_service.facturacion.aplicacion.VerificadorMedidor;
import co.edu.uptc.facturacion_service.grpc.ExisteMedidorRequest;
import co.edu.uptc.facturacion_service.grpc.MedidorGrpcGrpc.MedidorGrpcBlockingStub;
import org.springframework.stereotype.Component;

@Component
public class VerificadorMedidorGrpc implements VerificadorMedidor {

    private final MedidorGrpcBlockingStub stub;

    public VerificadorMedidorGrpc(MedidorGrpcBlockingStub stub) {
        this.stub = stub;
    }

    @Override
    public boolean existe(Long medidorId) {
        var request = ExisteMedidorRequest.newBuilder()
                .setMedidorId(medidorId)
                .build();
        return stub.existeMedidor(request).getExiste();
    }
}