package co.edu.uptc.facturacion_service.facturacion.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "medidores_locales")
public class MedidorLocal {

    @Id
    private Long id; // el mismo id que tiene el medidor en medicion-service

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(nullable = false)
    private boolean activo = true;

    protected MedidorLocal() {}

    public MedidorLocal(Long id, Long usuarioId, boolean activo) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.activo = activo;
    }

    public Long getId() { 
        return id; 
    }
    public Long getUsuarioId() { 
        return usuarioId; 
    }
    public boolean isActivo() { 
        return activo; 
    }

    public void asignarUsuario(Long usuarioId) { 
        this.usuarioId = usuarioId; 
    }
    public void desactivar() { 
        this.activo = false; 
    }
}