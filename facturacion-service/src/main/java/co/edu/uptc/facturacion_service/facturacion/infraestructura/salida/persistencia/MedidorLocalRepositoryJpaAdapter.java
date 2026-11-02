package co.edu.uptc.facturacion_service.facturacion.infraestructura.salida.persistencia;

import co.edu.uptc.facturacion_service.facturacion.aplicacion.RepositorioMedidoresLocales;
import co.edu.uptc.facturacion_service.facturacion.dominio.MedidorLocal;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class MedidorLocalRepositoryJpaAdapter implements RepositorioMedidoresLocales {

    private final MedidorLocalRepository repository;

    public MedidorLocalRepositoryJpaAdapter(MedidorLocalRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<MedidorLocal> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<MedidorLocal> listarActivos() {
        return repository.findByActivoTrue();
    }

    @Override
    public List<MedidorLocal> listarPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId);
    }

    @Override
    public MedidorLocal guardar(MedidorLocal medidor) {
        return repository.save(medidor);
    }
}