package co.edu.uptc.facturacion_service.facturacion.infraestructura.salida.persistencia;

import co.edu.uptc.facturacion_service.facturacion.aplicacion.RepositorioTarifas;
import co.edu.uptc.facturacion_service.facturacion.dominio.Tarifa;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TarifaRepositoryJpaAdapter implements RepositorioTarifas {

    private final TarifaRepository repository;

    public TarifaRepositoryJpaAdapter(TarifaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Tarifa guardar(Tarifa tarifa) {
        return repository.save(tarifa);
    }

    @Override
    public List<Tarifa> listar() {
        return repository.findAll();
    }
}