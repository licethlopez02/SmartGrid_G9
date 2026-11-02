package co.edu.uptc.facturacion_service.facturacion.dominio;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tarifas")
public class Tarifa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private TarifaVigente vigente;

    protected Tarifa() {
        
    }

    public Tarifa(TarifaVigente vigente) {
        this.vigente = vigente;
    }

    public Long getId() { 
        return id; 
    }
    
    public TarifaVigente vigente() { 
        return vigente; 
    }
}