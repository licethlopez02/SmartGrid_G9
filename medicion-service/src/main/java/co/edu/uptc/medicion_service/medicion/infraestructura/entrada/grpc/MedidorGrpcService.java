package co.edu.uptc.medicion_service.medicion.infraestructura.entrada.grpc;

import co.edu.uptc.medicion_service.grpc.*;
import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.salida.MedidorRepositoryPort;
import co.edu.uptc.medicion_service.medicion.aplicacion.puerto.entrada.ConsultarMedicionUseCase;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
public class MedidorGrpcService extends MedidorGrpcGrpc.MedidorGrpcImplBase {

    private final MedidorRepositoryPort repositorio;
    private final ConsultarMedicionUseCase consultas;

    public MedidorGrpcService(MedidorRepositoryPort repositorio, ConsultarMedicionUseCase consultas) {
        this.repositorio = repositorio;
        this.consultas = consultas;
    }

    @Override
    public void existeMedidor(ExisteMedidorRequest request,
                              StreamObserver<ExisteMedidorResponse> responseObserver) {
        boolean existe = repositorio.buscarPorId(request.getMedidorId()).isPresent();
        responseObserver.onNext(ExisteMedidorResponse.newBuilder().setExiste(existe).build());
        responseObserver.onCompleted();
    }

    @Override
    public void obtenerConsumo(ObtenerConsumoRequest request,
            StreamObserver<ObtenerConsumoResponse> responseObserver) {
        try {
            Instant desde = parsear(request.getDesde());
            Instant hasta = parsear(request.getHasta());
            var consumo = consultas.consumo(request.getMedidorId(), desde, hasta);
            boolean completo = consultas.periodoCompleto(request.getMedidorId(), desde, hasta);
            responseObserver.onNext(ObtenerConsumoResponse.newBuilder()
                    .setConsumo(consumo.total().toPlainString())
                    .setCantidadLecturas(consumo.cantidadLecturas())
                    .setPeriodoCompleto(completo).build());
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void periodoCompleto(PeriodoCompletoRequest request,
            StreamObserver<PeriodoCompletoResponse> responseObserver) {
        try {
            responseObserver.onNext(PeriodoCompletoResponse.newBuilder()
                    .setCompleto(consultas.periodoCompleto(request.getMedidorId(),
                            parsear(request.getDesde()), parsear(request.getHasta()))).build());
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void anomaliasNoResueltas(AnomaliasNoResueltasRequest request,
            StreamObserver<AnomaliasNoResueltasResponse> responseObserver) {
        try {
            var builder = AnomaliasNoResueltasResponse.newBuilder();
            consultas.anomaliasNoResueltas(request.getMedidorId(), parsear(request.getDesde()),
                    parsear(request.getHasta())).forEach(a -> builder.addAnomalias(AnomaliaGrpc.newBuilder()
                            .setId(a.id() == null ? 0 : a.id())
                            .setValor(a.valor().toPlainString())
                            .setTimestamp(a.timestamp().toString())
                            .setRazon(a.razon())
                            .setResuelta(a.resuelta()).build()));
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    private static Instant parsear(String valor) {
        return valor == null || valor.isBlank() ? null : Instant.parse(valor);
    }
}