package co.edu.uptc.facturacion_service.facturacion.aplicacion;

import co.edu.uptc.facturacion_service.facturacion.dominio.MedidorLocal;

import java.util.List;
import java.util.Optional;

public interface RepositorioMedidoresLocales {

    Optional<MedidorLocal> buscarPorId(Long id);

    List<MedidorLocal> listarActivos();

    List<MedidorLocal> listarPorUsuario(Long usuarioId);

    MedidorLocal guardar(MedidorLocal medidor);
}