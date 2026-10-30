package co.edu.uptc.medicion_service.medicion.infraestructura.entrada.grpc;

import co.edu.uptc.medicion_service.grpc.ExisteMedidorRequest;
import co.edu.uptc.medicion_service.grpc.ExisteMedidorResponse;
import co.edu.uptc.medicion_service.grpc.MedidorGrpcGrpc;
import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.MedidorRepositoryPort;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

@Service
public class MedidorGrpcService extends MedidorGrpcGrpc.MedidorGrpcImplBase {

    private final MedidorRepositoryPort repositorio;

    public MedidorGrpcService(MedidorRepositoryPort repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void existeMedidor(ExisteMedidorRequest request,
                              StreamObserver<ExisteMedidorResponse> responseObserver) {
        boolean existe = repositorio.buscarPorId(request.getMedidorId()).isPresent();
        responseObserver.onNext(ExisteMedidorResponse.newBuilder().setExiste(existe).build());
        responseObserver.onCompleted();
    }
}