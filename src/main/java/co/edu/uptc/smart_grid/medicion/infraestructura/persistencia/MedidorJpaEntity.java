package co.edu.uptc.smart_grid.medicion.infraestructura.persistencia;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "medidores")
public class MedidorJpaEntity {

	@Id
	private String id;

	@Column(nullable = false)
	private String ubicacion;

	@ElementCollection
	@CollectionTable(name = "lecturas_consumo", joinColumns = @JoinColumn(name = "medidor_id"))
	private List<LecturaJpaEmbeddable> lecturas = new ArrayList<>();

	protected MedidorJpaEntity() {
	}

	public MedidorJpaEntity(String id, String ubicacion, List<LecturaJpaEmbeddable> lecturas) {
		this.id = id;
		this.ubicacion = ubicacion;
		this.lecturas = new ArrayList<>(lecturas);
	}

	public String getId() {
		return id;
	}

	public String getUbicacion() {
		return ubicacion;
	}

	public List<LecturaJpaEmbeddable> getLecturas() {
		return List.copyOf(lecturas);
	}
}

@Embeddable
class LecturaJpaEmbeddable {

	@Column(name = "valor", nullable = false)
	private BigDecimal valor;

	@Column(name = "timestamp", nullable = false)
	private Instant timestamp;

	@Column(name = "lectura_medidor_id", nullable = false)
	private String medidorId;

	protected LecturaJpaEmbeddable() {
	}

	LecturaJpaEmbeddable(BigDecimal valor, Instant timestamp, String medidorId) {
		this.valor = valor;
		this.timestamp = timestamp;
		this.medidorId = medidorId;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public String getMedidorId() {
		return medidorId;
	}
}
