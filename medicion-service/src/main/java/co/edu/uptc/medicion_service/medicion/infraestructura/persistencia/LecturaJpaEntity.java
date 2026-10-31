package co.edu.uptc.medicion_service.medicion.infraestructura.persistencia;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "lecturas_consumo", indexes = @Index(name = "idx_lecturas_medidor_timestamp",
		columnList = "medidor_id,timestamp"))
public class LecturaJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "medidor_id", nullable = false)
	private String medidorId;

	@Column(name = "valor", nullable = false)
	private BigDecimal valor;

	@Column(name = "timestamp", nullable = false)
	private Instant timestamp;

	protected LecturaJpaEntity() {}

	public LecturaJpaEntity(String medidorId, BigDecimal valor, Instant timestamp) {
		this.medidorId = medidorId;
		this.valor = valor;
		this.timestamp = timestamp;
	}

	public String getMedidorId() { return medidorId; }
	public BigDecimal getValor() { return valor; }
	public Instant getTimestamp() { return timestamp; }
}
