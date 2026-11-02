package co.edu.uptc.facturacion_service.facturacion.aplicacion;

import co.edu.uptc.facturacion_service.facturacion.dominio.AccesoDenegadoException;
import co.edu.uptc.facturacion_service.facturacion.dominio.MedidorLocal;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccesoFacturasService {

    private final RepositorioMedidoresLocales medidores;

    public AccesoFacturasService(RepositorioMedidoresLocales medidores) {
        this.medidores = medidores;
    }

    public void verificarAcceso(Long usuarioId, boolean esOperador, Long medidorId) {
        if (esOperador) {
            return;
        }
        boolean esDueno = medidores.buscarPorId(medidorId)
                .map(m -> usuarioId != null && usuarioId.equals(m.getUsuarioId()))
                .orElse(false);
        if (!esDueno) {
            throw new AccesoDenegadoException();
        }
    }

    public List<Long> idsDeMedidoresDe(Long usuarioId) {
        return medidores.listarPorUsuario(usuarioId).stream().map(MedidorLocal::getId).toList();
    }
}
