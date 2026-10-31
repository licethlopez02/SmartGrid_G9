package co.edu.uptc.medicion_service.medicion.dominio;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A persisted anomaly keeps its own lifecycle, independently of the reading.
 */
public final class Anomalia {

	private final Long id;
	private final Long medidorId;
	private final BigDecimal valor;
	private final Instant timestamp;
	private final String razon;
	private boolean resuelta;

	public Anomalia(Long id, Long medidorId, BigDecimal valor, Instant timestamp,
			String razon, boolean resuelta) {
		this.id = id;
		this.medidorId = Objects.requireNonNull(medidorId, "El id del medidor es obligatorio");
		this.valor = Objects.requireNonNull(valor, "El valor es obligatorio");
		this.timestamp = Objects.requireNonNull(timestamp, "El timestamp es obligatorio");
		this.razon = Objects.requireNonNull(razon, "La razón es obligatoria");
		if (razon.isBlank()) {
			throw new IllegalArgumentException("La razón no puede estar vacía");
		}
		this.resuelta = resuelta;
	}

	public Long id() { return id; }
	public Long medidorId() { return medidorId; }
	public BigDecimal valor() { return valor; }
	public Instant timestamp() { return timestamp; }
	public String razon() { return razon; }
	public boolean resuelta() { return resuelta; }
	public void resolver() { this.resuelta = true; }
}
