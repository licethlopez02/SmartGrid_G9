package co.edu.uptc.facturacion_service;

import co.edu.uptc.facturacion_service.grpc.MedidorGrpcGrpc.MedidorGrpcBlockingStub;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.grpc.client.ImportGrpcClients;

@SpringBootApplication
@ImportGrpcClients(target = "medicion", types = MedidorGrpcBlockingStub.class)
public class FacturacionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FacturacionServiceApplication.class, args);
	}

}