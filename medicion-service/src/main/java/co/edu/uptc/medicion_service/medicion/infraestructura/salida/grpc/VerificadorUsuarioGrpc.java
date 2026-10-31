package co.edu.uptc.medicion_service.medicion.infraestructura.salida.grpc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.VerificadorUsuario;
import co.edu.uptc.usuarios_service.grpc.ExisteUsuarioActivoRequest;
import co.edu.uptc.usuarios_service.grpc.UsuarioGrpcGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Component
public class VerificadorUsuarioGrpc implements VerificadorUsuario {

	private final ManagedChannel channel;
	private final UsuarioGrpcGrpc.UsuarioGrpcBlockingStub stub;

	public VerificadorUsuarioGrpc(
			@Value("${usuarios.grpc.host:localhost}") String host,
			@Value("${usuarios.grpc.port:9091}") int port) {
		this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
		this.stub = UsuarioGrpcGrpc.newBlockingStub(channel);
	}

	@Override
	public boolean existeYEstaActivo(Long usuarioId) {
		if (usuarioId == null) {
			return false;
		}
		var respuesta = stub.existeUsuarioActivo(ExisteUsuarioActivoRequest.newBuilder()
				.setUsuarioId(usuarioId).build());
		return respuesta.getExiste() && respuesta.getActivo();
	}
}
