package com.TpFinal.dto.movimiento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.TpFinal.dto.BorradoLogico;
import com.TpFinal.dto.EstadoRegistro;
import com.TpFinal.dto.Identificable;
import com.TpFinal.dto.cobro.Cobro;
import com.TpFinal.dto.contrato.ContratoAlquiler;
import com.TpFinal.dto.contrato.ContratoVenta;
import com.TpFinal.dto.inmueble.TipoMoneda;


@Entity
@Table(name = "movimiento")
public class Movimiento implements Identificable, BorradoLogico{

	private static final String estadoRegistroS = "estadoRegistro";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	protected Long id;

	@Column(name = "descripcionMovimiento")
	private String descripcionMovimiento;

	@Column(name = "monto")
	private BigDecimal monto;

	@Column(name = "montoGananciaPropietario")
	private LocalDate fecha;

	@Column(name = Movimiento.estadoRegistroS)
	@NotNull
	private EstadoRegistro estadoRegistro;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipoMovimiento")
	private TipoMovimiento tipoMovimiento;

	@Enumerated(EnumType.STRING)
	@Column(name =  "claseMovimiento")
	private ClaseMovimiento claseMovimiento;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "tipoMoneda")
	private TipoMoneda tipoMoneda;

	@OneToOne(fetch=FetchType.EAGER)
	@Cascade({CascadeType.SAVE_UPDATE, CascadeType.MERGE})
	private Cobro cobro;

	public Movimiento() {

	}

	public Movimiento(Builder b) {
		this.descripcionMovimiento = b.descripcionMovimiento;
		this.monto = b.monto;
		this.fecha = b.fecha;
		this.estadoRegistro = b.estadoRegistro;
		this.tipoMovimiento = b.tipoMovimiento;
		this.claseMovimiento = b.claseMovimiento;
		this.cobro=b.cobro;
		this.tipoMoneda=b.tipoMoneda;
	}

	public static class Builder {

		private String descripcionMovimiento;
		private BigDecimal monto;
		private LocalDate fecha;
		private Cobro cobro;
		private EstadoRegistro estadoRegistro;
		private TipoMovimiento tipoMovimiento;
		private ClaseMovimiento claseMovimiento;
		private TipoMoneda tipoMoneda;

		public Builder setdescripcionMovimiento(String descripcionMovimiento) {
			this.descripcionMovimiento = descripcionMovimiento;
			return this;
		}

		public Builder setMonto(BigDecimal monto) {
			this.monto = monto;
			return this;
		}

		public Builder setFecha(LocalDate fecha) {
			this.fecha = fecha;
			return this;
		}

		public Builder setCobro(Cobro dato) {
			this.cobro=dato;
			return this;
		}

		public Builder setEstadoRegistro(EstadoRegistro estadoRegistro) {
			this.estadoRegistro = estadoRegistro;
			return this;
		}
		public Builder setTipoMovimiento(TipoMovimiento tipoMovimiento) {
			this.tipoMovimiento = tipoMovimiento;
			return this;
		}
		public Builder setClaseMovimiento(ClaseMovimiento claseMovimiento) {
			this.claseMovimiento = claseMovimiento;
			return this;
		}
		public Builder setTipoMoneda(TipoMoneda dato) {
			this.tipoMoneda=dato;
			return this;
		}

		public Movimiento build() {
			return new Movimiento(this);
		}

	}

	@Override
	public void setEstadoRegistro(EstadoRegistro estado) {
		this.estadoRegistro = estado;

	}

	@Override
	public EstadoRegistro getEstadoRegistro() {
		return this.estadoRegistro;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	public String getDescripcionMovimiento() {
		return descripcionMovimiento;
	}

	public void setDescripcionMovimiento(String descripcionMovimiento) {
		this.descripcionMovimiento = descripcionMovimiento;
	}

	public BigDecimal getMonto() {
		return monto;
	}

	public void setMonto(BigDecimal monto) {
		this.monto = monto;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public TipoMovimiento getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public ClaseMovimiento getClaseMovimiento() {
		return claseMovimiento;
	}

	public void setClaseMovimiento(ClaseMovimiento claseMovimiento) {
		this.claseMovimiento = claseMovimiento;
	}


	public void setId(Long id) {
		this.id = id;
	}

	public Cobro getCobro() {
		return cobro;
	}

	public void setCobro(Cobro cobro) {
		this.cobro = cobro;
	}
	
	public TipoMoneda getTipoMoneda() {
		return tipoMoneda;
	}

	public void setTipoMoneda(TipoMoneda tipoMoneda) {
		this.tipoMoneda = tipoMoneda;
	}

	@Override
	public int hashCode() {
		return 3;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Movimiento))
			return false;
		Movimiento movimiento = (Movimiento) o;
		return getId() != null && Objects.equals(getId(), movimiento.getId());
	}



}
