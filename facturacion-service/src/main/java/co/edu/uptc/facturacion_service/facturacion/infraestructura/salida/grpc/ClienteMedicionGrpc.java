package co.edu.uptc.facturacion_service.facturacion.infraestructura.salida.grpc;

import co.edu.uptc.facturacion_service.facturacion.aplicacion.ClienteMedicion;
import co.edu.uptc.facturacion_service.facturacion.aplicacion.ConsumoPeriodo;
import co.edu.uptc.facturacion_service.facturacion.aplicacion.ServicioNoDisponibleException;
import co.edu.uptc.facturacion_service.facturacion.dominio.AnomaliaFacturada;
import co.edu.uptc.facturacion_service.grpc.AnomaliasNoResueltasRequest;
import co.edu.uptc.facturacion_service.grpc.MedidorGrpcGrpc.MedidorGrpcBlockingStub;
import co.edu.uptc.facturacion_service.grpc.ObtenerConsumoRequest;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public class ClienteMedicionGrpc implements ClienteMedicion {

    private static final Logger log = LoggerFactory.getLogger(ClienteMedicionGrpc.class);
    private static final int MAX_RAZON = 255;

    private final MedidorGrpcBlockingStub stub;
    private final CircuitBreaker circuitBreaker;

    public ClienteMedicionGrpc(MedidorGrpcBlockingStub stub) {
        this.stub = stub;
        this.circuitBreaker = CircuitBreaker.of("medicion-service-consultas",
                CircuitBreakerConfig.custom()
                        .slidingWindowSize(10)
                        .minimumNumberOfCalls(5)
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(Duration.ofSeconds(10))
                        .recordException(e -> !(e instanceof StatusRuntimeException s
                                && s.getStatus().getCode() == Status.Code.INVALID_ARGUMENT))
                        .build());
        this.circuitBreaker.getEventPublisher().onStateTransition(
                e -> log.warn("Circuit Breaker medicion-service-consultas: {}", e.getStateTransition()));
    }

    @Override
    public ConsumoPeriodo obtenerConsumo(Long medidorId, LocalDate periodoInicio, LocalDate periodoFin) {
        var request = ObtenerConsumoRequest.newBuilder()
                .setMedidorId(medidorId)
                .setDesde(desde(periodoInicio))
                .setHasta(hasta(periodoFin))
                .build();
        var respuesta = llamar(() -> stub.withDeadlineAfter(5, TimeUnit.SECONDS).obtenerConsumo(request));
        return new ConsumoPeriodo(new BigDecimal(respuesta.getConsumo()),
                respuesta.getPeriodoCompleto(), respuesta.getCantidadLecturas());
    }

    @Override
    public List<AnomaliaFacturada> anomaliasNoResueltas(Long medidorId, LocalDate periodoInicio,
                                                         LocalDate periodoFin) {
        var request = AnomaliasNoResueltasRequest.newBuilder()
                .setMedidorId(medidorId)
                .setDesde(desde(periodoInicio))
                .setHasta(hasta(periodoFin))
                .build();
        var respuesta = llamar(() -> stub.withDeadlineAfter(5, TimeUnit.SECONDS).anomaliasNoResueltas(request));
        return respuesta.getAnomaliasList().stream()
                .map(a -> new AnomaliaFacturada(a.getId(), new BigDecimal(a.getValor()),
                        Instant.parse(a.getTimestamp()), recortar(a.getRazon())))
                .toList();
    }

    private <T> T llamar(Supplier<T> llamada) {
        try {
            return circuitBreaker.executeSupplier(llamada);
        } catch (CallNotPermittedException e) {
            throw new ServicioNoDisponibleException("medicion-service no está disponible (circuito abierto)", e);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.INVALID_ARGUMENT) {
                throw new IllegalArgumentException(e.getStatus().getDescription(), e);
            }
            throw new ServicioNoDisponibleException(
                    "No se pudo contactar a medicion-service: " + e.getStatus().getCode(), e);
        }
    }

    // Formato de fechas hacia medición: pendiente de confirmar con Dev 1 (qué va en "hasta").
    private static String desde(LocalDate periodoInicio) {
        return periodoInicio.atStartOfDay(ZoneOffset.UTC).toInstant().toString();
    }

    private static String hasta(LocalDate periodoFin) {
        return periodoFin.plusDays(1).atStartOfDay(ZoneOffset.UTC).minusSeconds(1).toInstant().toString();
    }

    private static String recortar(String razon) {
        return razon.length() > MAX_RAZON ? razon.substring(0, MAX_RAZON) : razon;
    }
}