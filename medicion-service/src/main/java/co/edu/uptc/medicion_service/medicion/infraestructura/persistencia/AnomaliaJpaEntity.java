package co.edu.uptc.medicion_service.medicion.infraestructura.persistencia;

import java.math.BigDecimal;
import java.time.Instant;

import co.edu.uptc.medicion_service.medicion.dominio.Anomalia;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "anomalias")
public class AnomaliaJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "medidor_id", nullable = false)
	private Long medidorId;

	@Column(nullable = false)
	private BigDecimal valor;

	@Column(nullable = false)
	private Instant timestamp;

	@Column(nullable = false, length = 1000)
	private String razon;

	@Column(nullable = false)
	private boolean resuelta;

	protected AnomaliaJpaEntity() {}

	public AnomaliaJpaEntity(Long medidorId, BigDecimal valor, Instant timestamp,
			String razon, boolean resuelta) {
		this.medidorId = medidorId;
		this.valor = valor;
		this.timestamp = timestamp;
		this.razon = razon;
		this.resuelta = resuelta;
	}

	public AnomaliaJpaEntity(Anomalia anomalia) {
		this(anomalia.medidorId(), anomalia.valor(), anomalia.timestamp(), anomalia.razon(),
				anomalia.resuelta());
		this.id = anomalia.id();
	}

	public Long getId() { return id; }
	public Long getMedidorId() { return medidorId; }
	public BigDecimal getValor() { return valor; }
	public Instant getTimestamp() { return timestamp; }
	public String getRazon() { return razon; }
	public boolean isResuelta() { return resuelta; }

	public void actualizar(Anomalia anomalia) {
		this.resuelta = anomalia.resuelta();
	}
}
