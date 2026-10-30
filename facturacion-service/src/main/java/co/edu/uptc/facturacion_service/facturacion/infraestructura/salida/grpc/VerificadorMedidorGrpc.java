package co.edu.uptc.facturacion_service.facturacion.infraestructura.salida.grpc;

import co.edu.uptc.facturacion_service.facturacion.aplicacion.ServicioNoDisponibleException;
import co.edu.uptc.facturacion_service.facturacion.aplicacion.VerificadorMedidor;
import co.edu.uptc.facturacion_service.grpc.ExisteMedidorRequest;
import co.edu.uptc.facturacion_service.grpc.MedidorGrpcGrpc.MedidorGrpcBlockingStub;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class VerificadorMedidorGrpc implements VerificadorMedidor {

    private static final Logger log = LoggerFactory.getLogger(VerificadorMedidorGrpc.class);

    private final MedidorGrpcBlockingStub stub;
    private final CircuitBreaker circuitBreaker;

    public VerificadorMedidorGrpc(MedidorGrpcBlockingStub stub) {
        this.stub = stub;
        this.circuitBreaker = CircuitBreaker.of("medicion-service",
                CircuitBreakerConfig.custom()
                        .slidingWindowSize(10)
                        .minimumNumberOfCalls(5)
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(Duration.ofSeconds(10))
                        .build());
        this.circuitBreaker.getEventPublisher().onStateTransition(
                e -> log.warn("Circuit Breaker medicion-service: {}", e.getStateTransition()));
    }

    @Override
    public boolean existe(Long medidorId) {
        try {
            return circuitBreaker.executeSupplier(() -> {
                var request = ExisteMedidorRequest.newBuilder()
                        .setMedidorId(medidorId)
                        .build();
                return stub.withDeadlineAfter(5, TimeUnit.SECONDS)
                        .existeMedidor(request)
                        .getExiste();
            });
        } catch (CallNotPermittedException e) {
            log.warn("Circuit Breaker ABIERTO: se rechaza la llamada sin intentar la red");
            throw new ServicioNoDisponibleException(
                    "medicion-service no está disponible (circuito abierto)", e);
        } catch (StatusRuntimeException e) {
            throw new ServicioNoDisponibleException(
                    "No se pudo contactar a medicion-service: " + e.getStatus().getCode(), e);
        }
    }
}