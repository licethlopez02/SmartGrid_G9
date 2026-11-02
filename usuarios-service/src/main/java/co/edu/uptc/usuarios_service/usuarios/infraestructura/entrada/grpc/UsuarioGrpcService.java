package co.edu.uptc.usuarios_service.usuarios.infraestructura.entrada.grpc;

import co.edu.uptc.usuarios_service.grpc.ExisteUsuarioActivoRequest;
import co.edu.uptc.usuarios_service.grpc.ExisteUsuarioActivoResponse;
import co.edu.uptc.usuarios_service.grpc.UsuarioGrpcGrpc;
import co.edu.uptc.usuarios_service.usuarios.aplicacion.RepositorioUsuarios;
import co.edu.uptc.usuarios_service.usuarios.dominio.Usuario;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

@Service
public class UsuarioGrpcService extends UsuarioGrpcGrpc.UsuarioGrpcImplBase {

    private final RepositorioUsuarios repositorio;

    public UsuarioGrpcService(RepositorioUsuarios repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void existeUsuarioActivo(ExisteUsuarioActivoRequest request,
                                    StreamObserver<ExisteUsuarioActivoResponse> responseObserver) {
        var usuario = repositorio.buscarPorId(request.getUsuarioId());
        responseObserver.onNext(ExisteUsuarioActivoResponse.newBuilder()
                .setExiste(usuario.isPresent())
                .setActivo(usuario.map(Usuario::isActivo).orElse(false))
                .build());
        responseObserver.onCompleted();
    }
}