package com.TpFinal.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.TpFinal.data.dao.DAOMovimientoImpl;
import com.TpFinal.data.dao.interfaces.DAOMovimiento;
import com.TpFinal.dto.EstadoRegistro;
import com.TpFinal.dto.cobro.Cobro;
import com.TpFinal.dto.cobro.EstadoCobro;
import com.TpFinal.dto.contrato.ContratoAlquiler;
import com.TpFinal.dto.movimiento.ClaseMovimiento;
import com.TpFinal.dto.movimiento.Movimiento;
import com.TpFinal.dto.movimiento.TipoMovimiento;
import com.TpFinal.view.reportes.ItemFichaMovimientos;
import com.TpFinal.view.reportes.ItemRepAlquileresACobrar;

public class MovimientoService {
	DAOMovimiento dao;
	private final static Logger logger = Logger.getLogger(MovimientoService.class);

	public MovimientoService() {
		dao = new DAOMovimientoImpl();
	}

	public boolean saveOrUpdate(Movimiento m) {
		return dao.saveOrUpdate(m);
	}

	public boolean delete(Movimiento m) {
		return dao.logicalDelete(m);
	}

	public List<Movimiento> readAll() {
		return dao.readAllActives();
	}

	public static Movimiento getInstanciaOtro() {
		return new Movimiento.Builder()
				.setdescripcionMovimiento("")
				.setMonto(BigDecimal.ZERO)
				.setFecha(LocalDate.now())
				.setClaseMovimiento(ClaseMovimiento.Otro)
				.setEstadoRegistro(EstadoRegistro.ACTIVO)
				.setTipoMovimiento(TipoMovimiento.Ingreso)
				.build();
	}

	public static Movimiento getInstanciaPagoAlquiler(Cobro c) {
		Movimiento ret= new Movimiento.Builder()
				.setdescripcionMovimiento(c.getContrato().getInmueble().toString())
				.setMonto(c.getMontoRecibido())
				.setFecha(LocalDate.now())
				.setClaseMovimiento(ClaseMovimiento.Alquiler)
				.setEstadoRegistro(EstadoRegistro.ACTIVO)
				.setTipoMovimiento(TipoMovimiento.Ingreso)
				.setTipoMoneda(c.getContrato().getMoneda())
				.setCobro(c)
				.build();
		return ret;
	}

	public static Movimiento getInstanciaPagoVenta(Cobro c) {
		Movimiento ret= new Movimiento.Builder()
				.setdescripcionMovimiento(c.getContrato().getInmueble().toString())
				.setMonto(c.getMontoRecibido())
				.setFecha(LocalDate.now())
				.setClaseMovimiento(ClaseMovimiento.Venta)
				.setEstadoRegistro(EstadoRegistro.ACTIVO)
				.setTipoMovimiento(TipoMovimiento.Ingreso)
				.setTipoMoneda(c.getContrato().getMoneda())
				.setCobro(c)
				.build();
		return ret;
	}

	public static Movimiento getInstanciaGananciaInmobiliaria(Cobro c) {
		Movimiento ret= new Movimiento.Builder()
				.setdescripcionMovimiento(c.getContrato().getInmueble().toString())
				.setMonto(c.getComision())
				.setFecha(LocalDate.now())
				.setClaseMovimiento(ClaseMovimiento.Comisión)
				.setEstadoRegistro(EstadoRegistro.ACTIVO)
				.setTipoMovimiento(TipoMovimiento.Ingreso)
				.setTipoMoneda(c.getContrato().getMoneda())
				.setCobro(c)
				.build();
		return ret;
	}

	public static Movimiento getInstanciaPagoAPropietario(Cobro c) {
		ContratoAlquiler c1=(ContratoAlquiler) c.getContrato();
		Movimiento ret= new Movimiento.Builder()
				.setdescripcionMovimiento(c.getContrato().getInmueble().toString())
				.setMonto(c.getMontoPropietario())
				.setFecha(LocalDate.now())
				.setClaseMovimiento(ClaseMovimiento.PagoAPropietario)
				.setEstadoRegistro(EstadoRegistro.ACTIVO)
				.setTipoMovimiento(TipoMovimiento.Egreso)
				.setTipoMoneda(c.getContrato().getMoneda())
				.setCobro(c)
				.build();
		return ret;
	}

	public List<Object> getListadoMovimientos(LocalDate fechaDesde, LocalDate fechaHasta, Integer refMensualAnual,
			TipoMovimiento tipoMov) {
		
		
		List<ItemFichaMovimientos> itemsReporte = new ArrayList<>();
		List<Movimiento> listaMovimientos = this.readAll();
		List<Movimiento> movimientos = new ArrayList<>();
		if (logger.isDebugEnabled()) {
			logger.debug("=================================================================");
			logger.debug("Movimientos cant: " + listaMovimientos.size());
		}

				movimientos.addAll(listaMovimientos.stream()
						.filter(c -> {
							return c.getTipoMovimiento().equals(tipoMov);
						})
						.filter(c -> {
							return fechaDesde != null ? c.getFecha().compareTo(fechaDesde) >= 0 : true;
						})
						.filter(c -> {
							return fechaHasta != null ? c.getFecha().compareTo(fechaHasta) <= 0 : true;
						})
						.collect(Collectors.toList()));
				
				if (logger.isDebugEnabled())
					logger.debug("Cantidad de movimientos obtenidos: " + movimientos.size());
		
				
		movimientos.forEach(mov -> {
					itemsReporte.add(new ItemFichaMovimientos(mov, refMensualAnual));
				});
			
	
		itemsReporte.sort(Comparator.comparing(ItemFichaMovimientos::getAnio).reversed()
				.thenComparing(ItemFichaMovimientos::getClaseMovimiento));
				
		if (logger.isDebugEnabled()) {
			logger.debug("=====================================================");
			logger.debug("Cantidad de items obtenidos: " + itemsReporte.size());
		}
		
		return itemsReporte.stream().map(i -> (Object) i).collect(Collectors.toList());
		
	}


}
