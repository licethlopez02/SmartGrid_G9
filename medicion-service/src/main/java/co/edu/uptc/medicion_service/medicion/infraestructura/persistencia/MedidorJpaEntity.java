package co.edu.uptc.medicion_service.medicion.infraestructura.persistencia;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "medidores", uniqueConstraints = @UniqueConstraint(
		name = "uk_medidor_propiedad_estado", columnNames = {"propiedad", "estado"}))
public class MedidorJpaEntity {

	@Id
	private String id;

	@Column(nullable = false)
	private String ubicacion;

	@Column(name = "usuario_id")
	private Long usuarioId;

	@Column(name = "propiedad", nullable = false)
	private String propiedad;

	@Column(name = "estado", columnDefinition = "varchar(20) default 'ACTIVO'")
	private String estado = "ACTIVO";

	protected MedidorJpaEntity() {
	}

	public MedidorJpaEntity(String id, String ubicacion, Long usuarioId, String propiedad, String estado) {
		this.id = id;
		this.ubicacion = ubicacion;
		this.usuarioId = usuarioId;
		this.propiedad = propiedad;
		this.estado = estado;
	}

	public String getId() {
		return id;
	}

	public String getUbicacion() {
		return ubicacion;
	}

	public String getEstado() {
		return estado;
	}

	public Long getUsuarioId() { return usuarioId; }
	public String getPropiedad() { return propiedad; }
}
