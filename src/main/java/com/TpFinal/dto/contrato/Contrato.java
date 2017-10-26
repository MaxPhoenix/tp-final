package com.TpFinal.dto.contrato;

import java.sql.Blob;
import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.TpFinal.dto.BorradoLogico;
import com.TpFinal.dto.EstadoRegistro;
import com.TpFinal.dto.Identificable;
import com.TpFinal.dto.inmueble.Inmueble;
import com.TpFinal.dto.inmueble.TipoMoneda;

@Entity
@Table(name = "contrato")
@Inheritance(strategy = InheritanceType.JOINED)
public class Contrato implements Identificable, BorradoLogico {
    private static final String estadoRegistroS = "estadoRegistro";
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    protected Long id;
    @Column(name = "fechaCelebracion")
    protected LocalDate fechaCelebracion;
    @Column(name = "documento")
    protected Blob documento;
    @Enumerated(EnumType.STRING)
    @Column(name = Contrato.estadoRegistroS)
    private EstadoRegistro estadoRegistro;
    @ManyToOne(fetch = FetchType.EAGER)
    @Cascade({ CascadeType.SAVE_UPDATE, CascadeType.MERGE })
    @JoinColumn(name = "id_inmueble")
    @NotNull
    protected Inmueble inmueble;
    @Enumerated(EnumType.STRING)
    @Column(name = "moneda")
    private TipoMoneda moneda;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_contrato")
    private EstadoContrato estadoContrato;

    public Contrato() {
    }

    public Contrato(Long id, LocalDate fechaCelebracion, Blob documento, EstadoRegistro estado, Inmueble inmueble) {
	super();
	this.id = id;
	this.fechaCelebracion = fechaCelebracion;
	this.documento = documento;
	this.estadoRegistro = estado;
	this.inmueble = inmueble;
	this.moneda = TipoMoneda.Pesos;
	this.estadoContrato = EstadoContrato.EnProcesoDeCarga;
    }

    @Override
    public Long getId() {
	return this.id;
    }

    public EstadoContrato getEstadoContrato() {
	return estadoContrato;
    }

    public void setEstadoContrato(EstadoContrato estadoContrato) {
	this.estadoContrato = estadoContrato;
    }

    public TipoMoneda getMoneda() {
	return moneda;
    }



    public void setMoneda(TipoMoneda moneda) {
	this.moneda = moneda;
    }

    public LocalDate getFechaCelebracion() {
	return fechaCelebracion;
    }

    public void setFechaCelebracion(LocalDate fechaCelebracion) {
	this.fechaCelebracion = fechaCelebracion;
    }

    public Blob getDocumento() {
	return documento;
    }

    public void setDocumento(Blob documento) {
	this.documento = documento;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public Inmueble getInmueble() {
	return inmueble;
    }

    public void setInmueble(Inmueble inmueble) {
	if (this.inmueble != null && !this.inmueble.equals(inmueble)) {
	    this.inmueble.removeContrato(this);
	}
	this.inmueble = inmueble;
	if (inmueble != null && !inmueble.getContratos().contains(this))
	    inmueble.addContrato(this);
    }

    @Override
    public void setEstadoRegistro(EstadoRegistro estado) {
	this.estadoRegistro = estado;

    }

    @Override
    public EstadoRegistro getEstadoRegistro() {
	return estadoRegistro;
    }

    @Override
    public boolean equals(Object o) {
	if (this == o)
	    return true;
	if (!(o instanceof Contrato))
	    return false;
	Contrato contrato = (Contrato) o;
	return getId() != null && Objects.equals(getId(), contrato.getId());
    }

    @Override
    public int hashCode() {
	return 37;
    }

}
