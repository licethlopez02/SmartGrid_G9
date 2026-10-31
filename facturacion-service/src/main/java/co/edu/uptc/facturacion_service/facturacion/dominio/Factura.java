package co.edu.uptc.facturacion_service.facturacion.dominio;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "facturas")
public class Factura {

    public static final int DIAS_PARA_VENCER = 15;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "medidor_id", nullable = false)
    private Long medidorId;

    @Column(name = "periodo_inicio", nullable = false)
    private LocalDate periodoInicio;

    @Column(name = "periodo_fin", nullable = false)
    private LocalDate periodoFin;

    @Column(name = "consumo_acumulado_kwh", nullable = false)
    private BigDecimal consumoAcumuladoKwh;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "tarifa_valor", nullable = false))
    @AttributeOverride(name = "vigenciaDesde", column = @Column(name = "tarifa_vigencia_desde", nullable = false))
    @AttributeOverride(name = "vigenciaHasta", column = @Column(name = "tarifa_vigencia_hasta"))
    private TarifaVigente tarifaAplicada;

    @Column(name = "monto_total", nullable = false)
    private BigDecimal montoTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoFactura estado;

    @Column(name = "fecha_generacion", nullable = false)
    private LocalDate fechaGeneracion;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "huerfana", nullable = false, columnDefinition = "boolean default false")
    private boolean huerfana;

    protected Factura() {}

    public Factura(Long medidorId, LocalDate periodoInicio, LocalDate periodoFin,
            BigDecimal consumoAcumuladoKwh, TarifaVigente tarifaAplicada, LocalDate fechaGeneracion) {
        this.medidorId = medidorId;
        this.periodoInicio = periodoInicio;
        this.periodoFin = periodoFin;
        this.consumoAcumuladoKwh = consumoAcumuladoKwh;
        this.tarifaAplicada = tarifaAplicada;
        this.montoTotal = consumoAcumuladoKwh.multiply(tarifaAplicada.valor());
        this.fechaGeneracion = fechaGeneracion;
        this.fechaVencimiento = fechaGeneracion.plusDays(DIAS_PARA_VENCER);
        this.estado = EstadoFactura.PENDIENTE;
    }

    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMedidorId() {
        return medidorId;
    }

    public LocalDate getPeriodoInicio() {
        return periodoInicio;
    }

    public LocalDate getPeriodoFin() {
        return periodoFin;
    }

    public BigDecimal getConsumoAcumuladoKwh() {
        return consumoAcumuladoKwh;
    }

    public TarifaVigente getTarifaAplicada() {
        return tarifaAplicada;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public EstadoFactura getEstado() {
        return estado;
    }

    public LocalDate getFechaGeneracion() {
        return fechaGeneracion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Factura other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() { 
        return getClass().hashCode(); 
    }

    public boolean isHuerfana() {
        return huerfana;
    }

    public void marcarComoHuerfana() {
        this.huerfana = true;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public EstadoFactura estadoActual(LocalDate hoy) {
        if (estado == EstadoFactura.PENDIENTE && fechaVencimiento != null && hoy.isAfter(fechaVencimiento)) {
            return EstadoFactura.VENCIDA;
        }
        return estado;
    }
}