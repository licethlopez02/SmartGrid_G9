package co.edu.uptc.facturacion_service.facturacion.aplicacion;

import co.edu.uptc.facturacion_service.facturacion.dominio.Tarifa;
import co.edu.uptc.facturacion_service.facturacion.dominio.TarifaSuperpuestaException;
import co.edu.uptc.facturacion_service.facturacion.dominio.TarifaVigente;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TarifaService implements TarifaUseCase {

    private final RepositorioTarifas repositorioTarifas;

    public TarifaService(RepositorioTarifas repositorioTarifas) {
        this.repositorioTarifas = repositorioTarifas;
    }

    @Override
    public Tarifa registrar(BigDecimal valor, LocalDate vigenciaDesde, LocalDate vigenciaHasta) {
        TarifaVigente nueva = new TarifaVigente(valor, vigenciaDesde, vigenciaHasta);
        boolean seSuperpone = repositorioTarifas.listar().stream()
                .anyMatch(t -> t.vigente().seSuperponeCon(nueva));
        if (seSuperpone) {
            throw new TarifaSuperpuestaException();
        }
        return repositorioTarifas.guardar(new Tarifa(nueva));
    }

    @Override
    public List<Tarifa> listar() {
        return repositorioTarifas.listar();
    }
}