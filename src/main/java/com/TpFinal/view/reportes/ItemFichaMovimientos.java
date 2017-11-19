package com.TpFinal.view.reportes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.TpFinal.dto.contrato.ContratoAlquiler;
import com.TpFinal.dto.contrato.ContratoVenta;
import com.TpFinal.dto.inmueble.TipoMoneda;
import com.TpFinal.dto.movimiento.Movimiento;

public class ItemFichaMovimientos {
    private static final Logger logger = Logger.getLogger(ItemFichaMovimientos.class);
    String claseMovimiento;
    String tipoMovimiento;
    String descripción;
    BigDecimal monto;
    String fecha;
    String simbolo;
    Integer anio;
    String mes;
    LocalDate fechaLocalDate;
    Integer refTipoRep;
    String RepAnualMensual;

    public ItemFichaMovimientos(Movimiento m, Integer i) {
	this.refTipoRep = i;
	setearTipoReporte(i);
	this.fechaLocalDate = m.getFecha();
	this.claseMovimiento = String.valueOf(m.getClaseMovimiento());
	this.tipoMovimiento = String.valueOf(m.getTipoMovimiento());
	this.descripción = m.getDescripcionMovimiento();
	this.monto = m.getMonto();
	castearContrato(m);
	this.anio = fechaLocalDate.getYear();
	this.mes = formatearMes(fechaLocalDate.getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag(
		"es-AR")));
	this.fecha = m.getFecha().format(new DateTimeFormatterBuilder().appendPattern("dd/MM/YYYY").toFormatter());
    }

    public String getClaseMovimiento() {
	return claseMovimiento;
    }

    public void setClaseMovimiento(String claseMovimiento) {
	this.claseMovimiento = claseMovimiento;
    }

    public String getTipoMovimiento() {
	return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
	this.tipoMovimiento = tipoMovimiento;
    }

    public String getDescripción() {
	return descripción;
    }

    public void setDescripción(String descripción) {
	this.descripción = descripción;
    }

    public BigDecimal getMonto() {
	return monto;
    }

    public void setMonto(BigDecimal monto) {
	this.monto = monto;
    }

    public String getFecha() {
	return fecha;
    }

    public void setFecha(String fecha) {
	this.fecha = fecha;
    }

    public String getSimbolo() {
	return simbolo;
    }

    public void setSimbolo(String simbolo) {
	this.simbolo = simbolo;
    }

    public Integer getAnio() {
	return anio;
    }

    public void setAnio(Integer anio) {
	this.anio = anio;
    }

    public String getMes() {
	return mes;
    }

    public void setMes(String mes) {
	this.mes = mes;
    }

    public Integer getRefTipoRep() {
	return refTipoRep;
    }

    public void setRefTipoRep(Integer refTipoRep) {
	this.refTipoRep = refTipoRep;
    }

    public String getRepAnualMensual() {
	return RepAnualMensual;
    }

    public void setRepAnualMensual(String repAnualMensual) {
	RepAnualMensual = repAnualMensual;
    }

    public void setearTipoReporte(Integer i) {
	if (i.equals(1)) {
	    RepAnualMensual = "Mensual";
	}

	if (i.equals(2)) {
	    RepAnualMensual = "Anual";
	}

    }

    public void castearContrato(Movimiento m) {
	if (logger.isDebugEnabled()) {
	    String movString = "nulo";
	    String cobroString = "nulo";
	    String contratoString = "nulo";
	    String monedaString = "nulo";

	    if (m != null) {
		movString = m.toString();
		if (m.getCobro() != null) {
		    cobroString = m.getCobro().toString();
		    if (m.getCobro().getContrato() != null) {
			contratoString = m.getCobro().getContrato().toString();
			if (m.getCobro().getContrato().getMoneda() != null) {
			    monedaString = m.getCobro().getContrato().getMoneda().toString();
			}
		    }
		}
	    }
	    logger.debug("Movimiento: " + movString);
	    logger.debug("Cobro: " + cobroString);
	    logger.debug("Contrato: " + contratoString);
	    logger.debug("Moneda: " + monedaString);
	}
	if (m.getCobro().getContrato() instanceof ContratoAlquiler) {
	    this.simbolo = TipoMoneda.getSimbolo(m.getCobro().getContrato().getMoneda());
	}

	if (m.getCobro().getContrato() instanceof ContratoVenta) {
	    this.simbolo = TipoMoneda.getSimbolo(m.getCobro().getContrato().getMoneda());
	}
    }

    public String formatearMes(String mes) {

	String mesFormateado = "";

	mesFormateado = mes.toUpperCase().substring(0, 1) + mes.toLowerCase().substring(1);

	return mesFormateado;
    }

}
