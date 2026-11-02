package co.edu.uptc.facturacion_service.facturacion.aplicacion;

import co.edu.uptc.facturacion_service.facturacion.dominio.MedidorLocal;
import org.springframework.stereotype.Service;

@Service
public class SincronizacionMedidoresService {

    private final RepositorioMedidoresLocales repositorio;

    public SincronizacionMedidoresService(RepositorioMedidoresLocales repositorio) {
        this.repositorio = repositorio;
    }

    public void registrar(Long medidorId, Long usuarioId) {
        repositorio.buscarPorId(medidorId).ifPresentOrElse(
                existente -> {
                    if (usuarioId != null) {
                        existente.asignarUsuario(usuarioId);
                    }
                    repositorio.guardar(existente);
                },
                () -> repositorio.guardar(new MedidorLocal(medidorId, usuarioId, true)));
    }

    public void desactivar(Long medidorId) {
        MedidorLocal medidor = repositorio.buscarPorId(medidorId)
                .orElseGet(() -> new MedidorLocal(medidorId, null, true));
        medidor.desactivar();
        repositorio.guardar(medidor);
    }
}