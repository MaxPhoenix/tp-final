package com.TpFinal.dto.parametrosSistema;

import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.apache.log4j.Logger;
import com.TpFinal.dto.BorradoLogico;
import com.TpFinal.dto.EstadoRegistro;
import com.TpFinal.dto.Identificable;

@Entity
@Table(name = "parametros")
public class ParametrosSistema implements Identificable, BorradoLogico {
    private static Logger logger = Logger.getLogger(ParametrosSistema.class);
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_ParametroSistema")
    private Long id;
    @Column(name = "proximo_a_vencer")
    Integer diasProximoAVencer = 30;
    @Column(name = "dia_de_pago")
    Integer diaDePago = 10;
    @Column(name = "cant_minima_certificados")
    Integer cantMinimaCertificados = 2;
    @Column(name = "valor_certificado")
    BigDecimal valorCertificado = BigDecimal.valueOf(1900);
    @Column(name = "comision_a_propietario")
    Integer comisionAPropietario = 4;
    @Column(name = "comision_a_inquilino")
    Integer comisionAInquilino = 3;
    @Column(name = "comision_cobro")
    Double comisionCobro = 0.06;
    @Column(name = "frecuencia_aviso_A")
    Integer frecuenciaAvisoCategoriaA = 5;
    @Column(name = "frecuencia_aviso_B")
    Integer frecuenciaAvisoCategoriaB = 15;
    @Column(name = "frecuencia_aviso_C")
    Integer frecuenciaAvisoCategoriaC = 10;
    @Column(name = "frecuencia_aviso_D")
    Integer frecuenciaAvisoCategoriaD = 20;

    public ParametrosSistema() {

    }

    public Integer getProximoAVencer() {
	return diasProximoAVencer;
    }

    public void setProximoAVencer(Integer proximoAVencer) {
	this.diasProximoAVencer = proximoAVencer;
    }

    public Integer getDiaDePago() {
	return diaDePago;
    }

    public void setDiaDePago(Integer diaDePago) {
	this.diaDePago = diaDePago;
    }

    public Integer getCantMinimaCertificados() {
	return cantMinimaCertificados;
    }

    public void setCantMinimaCertificados(Integer cantMinimaCertificados) {
	this.cantMinimaCertificados = cantMinimaCertificados;
    }

    public BigDecimal getValorCertificado() {
	return valorCertificado;
    }

    public void setValorCertificado(BigDecimal valorCertificado) {
	this.valorCertificado = valorCertificado;
    }

    public Integer getComisionAPropietario() {
	return comisionAPropietario;
    }

    public void setComisionAPropietario(Integer comisionAPropietario) {
	this.comisionAPropietario = comisionAPropietario;
    }

    public Integer getComisionAInquilino() {
	return comisionAInquilino;
    }

    public void setComisionAInquilino(Integer comisionAInquilino) {
	this.comisionAInquilino = comisionAInquilino;
    }

    public Double getComisionCobro() {
	return comisionCobro;
    }

    public void setComisionCobro(Double comisionCobro) {
	this.comisionCobro = comisionCobro;
    }

    public Integer getFrecuenciaAvisoCategoriaA() {
	return frecuenciaAvisoCategoriaA;
    }

    public void setFrecuenciaAvisoCategoriaA(Integer frecuenciaAvisoCategoriaA) {
	this.frecuenciaAvisoCategoriaA = frecuenciaAvisoCategoriaA;
    }

    public Integer getFrecuenciaAvisoCategoriaB() {
	return frecuenciaAvisoCategoriaB;
    }

    public void setFrecuenciaAvisoCategoriaB(Integer frecuenciaAvisoCategoriaB) {
	this.frecuenciaAvisoCategoriaB = frecuenciaAvisoCategoriaB;
    }

    public Integer getFrecuenciaAvisoCategoriaC() {
	return frecuenciaAvisoCategoriaC;
    }

    public void setFrecuenciaAvisoCategoriaC(Integer frecuenciaAvisoCategoriaC) {
	this.frecuenciaAvisoCategoriaC = frecuenciaAvisoCategoriaC;
    }

    public Integer getFrecuenciaAvisoCategoriaD() {
	return frecuenciaAvisoCategoriaD;
    }

    public void setFrecuenciaAvisoCategoriaD(Integer frecuenciaAvisoCategoriaD) {
	this.frecuenciaAvisoCategoriaD = frecuenciaAvisoCategoriaD;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Override
    public Long getId() {
	return id;
    }

    @Override
    public int hashCode() {
	return 31;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (!(obj instanceof ParametrosSistema))
	    return false;
	ParametrosSistema other = (ParametrosSistema) obj;
	return getId() != null && Objects.equals(getId(), other.getId());
    }

    @Override
    public void setEstadoRegistro(EstadoRegistro estado) {
    }

    @Override
    public EstadoRegistro getEstadoRegistro() {
	return EstadoRegistro.ACTIVO;
    }

    @Override
    public String toString() {
	return "ParametroSistema [\nid=" + id + "\nproximoAVencer=" + diasProximoAVencer + "\ndiaDePago=" + diaDePago
		+ "\ncantMinimaCertificados=" + cantMinimaCertificados + "\nvalorCertificado=" + valorCertificado
		+ "\ncomisionAPropietario=" + comisionAPropietario + "\ncomisionAInquilino=" + comisionAInquilino
		+ "\ncomisionCobro=" + comisionCobro + "\nfrecuenciaAvisoCategoriaA=" + frecuenciaAvisoCategoriaA
		+ "\nfrecuenciaAvisoCategoriaB=" + frecuenciaAvisoCategoriaB + "\nfrecuenciaAvisoCategoriaC="
		+ frecuenciaAvisoCategoriaC + "\nfrecuenciaAvisoCategoriaD=" + frecuenciaAvisoCategoriaD + "\n]";
    }
    
    

}
