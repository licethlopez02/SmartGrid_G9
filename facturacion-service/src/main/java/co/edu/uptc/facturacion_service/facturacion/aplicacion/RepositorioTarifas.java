package co.edu.uptc.facturacion_service.facturacion.aplicacion;

import co.edu.uptc.facturacion_service.facturacion.dominio.Tarifa;

import java.util.List;

public interface RepositorioTarifas {
    Tarifa guardar(Tarifa tarifa);
    List<Tarifa> listar();
}