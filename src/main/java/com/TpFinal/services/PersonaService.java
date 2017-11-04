package com.TpFinal.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.TpFinal.data.dao.DAOPersonaImpl;
import com.TpFinal.data.dao.interfaces.DAOPersona;
import com.TpFinal.dto.EstadoRegistro;
import com.TpFinal.dto.cobro.Cobro;
import com.TpFinal.dto.contrato.ContratoAlquiler;
import com.TpFinal.dto.contrato.EstadoContrato;
import com.TpFinal.dto.persona.Calificacion;
import com.TpFinal.dto.persona.Credencial;
import com.TpFinal.dto.persona.Empleado;
import com.TpFinal.dto.persona.EstadoEmpleado;
import com.TpFinal.dto.persona.Inquilino;
import com.TpFinal.dto.persona.Persona;
import com.TpFinal.dto.persona.Rol;
import com.TpFinal.view.persona.FiltroEmpleados;
import com.TpFinal.view.persona.FiltroInteresados;
import com.itextpdf.text.log.SysoCounter;

public class PersonaService {
	DAOPersona dao;
	CredencialService credencialService = new CredencialService();

	public PersonaService() {
		dao = new DAOPersonaImpl();
	}

	public boolean saveOrUpdate(Persona p) {
		return dao.saveOrUpdate(p);
	}

	public boolean delete(Persona p) {
		if (p.giveMeYourRoles().contains(Rol.Empleado)) {
			Credencial c = ((Empleado) p.getRol(Rol.Empleado)).getCredencial();
			if (c != null) {
				credencialService.deepDelete(c);
			}
		}
		return dao.logicalDelete(p);
	}

	public List<Persona> readAll() {
		return dao.readAllActives();
	}

	public List<Persona> getInmobiliarias() {
		return dao.readAllActives().stream()
				.filter(p -> p.getEsInmobiliaria())
				.collect(Collectors.toList());
	}

	public List<Persona> getPersonas() {
		return dao.readAllActives().stream()
				.filter(p -> !p.getEsInmobiliaria())
				.collect(Collectors.toList());
	}

	public Calificacion calificarInquilino(Persona p) {
		Calificacion ret=null;
		Integer maximoAcumulado=0;
		Integer maximoMeses=0;
		Integer cantidadMesesComoInquilino=0;
		List<ContratoAlquiler> vigentes=getContratosVigentes(p);
		if(p.getInquilino()!=null && p.getInquilino().getContratos()!=null && p.getInquilino().getContratos().size()!=0) {
			for(ContratoAlquiler ca:vigentes) {
				List<Cobro>cobros = ca.getCobros().stream().collect(Collectors.toList());
				cobros=this.filtrarUltimosSeisMeses(cobros);
				new CobroService().calcularDatosFaltantes(cobros);
				cobros.sort((c1,c2)-> c1.getFechaDeVencimiento().compareTo(c2.getFechaDeVencimiento()));
				
				if(cantidadConsecutivos(cobros)>maximoAcumulado)
					maximoAcumulado=cantidadConsecutivos(cobros);
				if(cantidadPagosAtrasados(cobros)>maximoMeses)
					maximoMeses=cantidadPagosAtrasados(cobros);
				if(cantidadDeMesesComoInquilino(cobros)<cantidadMesesComoInquilino)
					cantidadMesesComoInquilino=cantidadDeMesesComoInquilino(cobros);
			}	
			
			ret = calcular(p, maximoAcumulado, maximoMeses, cantidadMesesComoInquilino);
			
		}
		return ret;
	}

	private Calificacion calcular(Persona p, Integer maximoAcumulado, Integer maximoMeses,
			Integer cantidadMesesComoInquilino) {
		Calificacion ret;
		if(maximoAcumulado>=4) {
			p.getInquilino().setCalificacion(Calificacion.A);
			ret=Calificacion.A;
		}else if(maximoMeses>0 && maximoMeses<=3 && cantidadMesesComoInquilino<6 ) {
			p.getInquilino().setCalificacion(Calificacion.C);
			ret=Calificacion.C;
		}else if(maximoMeses>=3) {
			p.getInquilino().setCalificacion(Calificacion.B);
			ret=Calificacion.B;
		}else {
			p.getInquilino().setCalificacion(Calificacion.D);
			ret=Calificacion.D;
		}
		return ret;
	}

	public List<ContratoAlquiler> getContratosVigentes(Persona p) {
		List<ContratoAlquiler> ret=new ArrayList<>();
		if(p.getInquilino()!=null && p.getInquilino().getContratos()!=null && p.getInquilino().getContratos().size()!=0) {
			ret=p.getInquilino().getContratos().stream()
					.filter(c -> c.getEstadoContrato().equals(EstadoContrato.Vigente))
					.collect(Collectors.toList());
		}
		return ret;
	}

	public Integer cantidadPagosAtrasados(List<Cobro> c) {

		List<Cobro>cobros=c.stream()
				.filter(cob -> cobroAtrasado(cob))
				.collect(Collectors.toList());

		return cobros.size();
	}

	public Integer cantidadConsecutivos(List<Cobro> c) {
		Integer cantidad=0;
		Integer aux=0;
		for(Cobro cobro: c) {
			if(cobroAtrasado(cobro)) {
				aux++;
				if(aux>cantidad)
					cantidad=aux;
			}else
				aux=0;
		}
		return cantidad;
	}

	public boolean cobroAtrasado(Cobro c) {
		boolean ret=false;
		if(c.getFechaDeVencimiento().compareTo(LocalDate.now())<0) {
			if(c.getFechaDePago()==null)
				ret=true;
			else if(c.getFechaDePago().compareTo(c.getFechaDeVencimiento())>0)
				ret=true;
		}
		return ret;

	}

	public List<Cobro> filtrarUltimosSeisMeses(List<Cobro>cobros) {
		LocalDate fecha = haceSeisMeses(cobros.get(0).getFechaDeVencimiento().getDayOfMonth());
		return cobros.stream()
				.filter(cob -> cob.getFechaDeVencimiento().compareTo(fecha)>0 || cob.getFechaDeVencimiento().compareTo(fecha)==0)
				.collect(Collectors.toList());
	}

	public Integer cantidadDeMesesComoInquilino(List<Cobro>cobros) {
		LocalDate hoy= LocalDate.now();
		hoy=hoy.withDayOfMonth(1);
		LocalDate diaPrimerCobro=cobros.get(0).getFechaDeVencimiento();
		diaPrimerCobro=diaPrimerCobro.withDayOfMonth(1);

		return (int) ChronoUnit.MONTHS.between(hoy, diaPrimerCobro);
	}

	private LocalDate haceSeisMeses(Integer diaCobro) {
		LocalDate fecha = LocalDate.now();
		if(fecha.getDayOfMonth()> diaCobro) {
			fecha=fecha.withDayOfMonth(1);
			fecha=fecha.minusMonths(5);
		}else {
			fecha=fecha.withDayOfMonth(1);
			fecha=fecha.minusMonths(6);
		}

		return fecha;
	}

	public synchronized List<Persona> findAll(String stringFilter) {
		ArrayList arrayList = new ArrayList();
		List<Persona> personas = dao.readAllActives();
		if (stringFilter != "") {

			for (Persona persona : personas) {

				boolean passesFilter = (stringFilter == null || stringFilter.isEmpty())
						|| persona.toString().toLowerCase()
						.contains(stringFilter.toLowerCase());
				if (passesFilter) {

					arrayList.add(persona);
				}

			}
		} else {
			arrayList.addAll(personas);
		}

		Collections.sort(arrayList, new Comparator<Persona>() {

			@Override
			public int compare(Persona o1, Persona o2) {
				return (int) (o2.getId() - o1.getId());
			}
		});
		return arrayList;
	}

	public synchronized List<Persona> findForRole(String stringFilter) {
		ArrayList arrayList = new ArrayList();
		List<Persona> personas = dao.readAllActives();
		if (stringFilter != "") {

			for (Persona persona : personas) {

				boolean passesFilter = (stringFilter == null || stringFilter.isEmpty())
						|| persona.roles().toLowerCase()
						.contains(stringFilter.toLowerCase());
				if (passesFilter) {

					arrayList.add(persona);
				}

			}
		} else {
			arrayList.addAll(personas);
		}

		Collections.sort(arrayList, new Comparator<Persona>() {

			@Override
			public int compare(Persona o1, Persona o2) {
				return (int) (o2.getId() - o1.getId());
			}
		});
		return arrayList;
	}

	public static Inquilino getPersonaConInquilino() {
		Persona p = new Persona.Builder().setNombre("").setApellido("").build();
		Inquilino i = new Inquilino.Builder().setPersona(p).build();
		p.getRoles().add(i);
		return i;
	}

	public List<Persona> findAllClientes(FiltroInteresados filtro) {
		List<Persona> personas = dao.readAllActives().stream()
				.filter(p -> !p.giveMeYourRoles().contains(Rol.Empleado))
				.filter(filtro.getFiltroCompuesto()).collect(Collectors.toList());
		personas.sort(Comparator.comparing(Persona::getId));
		return personas;
	}

	public List<Empleado> findAllEmpleados(FiltroEmpleados filtro) {
		List<Empleado> empleados = dao.readAll().stream()
				.filter(p -> p.giveMeYourRoles().contains(Rol.Empleado))		
				.map(p -> (Empleado) p.getRol(Rol.Empleado))
				.filter(filtro.getFiltroCompuesto())
				.collect(Collectors.toList());
		empleados.sort(Comparator.comparing(Empleado::getId));
		return empleados;
	}

	public static Empleado getEmpleadoInstancia() {
		Persona p = new Persona();
		Empleado e = new Empleado.Builder()
				.setFechaDeAlta(LocalDate.now())
				.setPersona(p)
				.build();
		Credencial c = new Credencial.Builder().setEmpleado(e).build();
		e.setCredencial(c);
		return e;
	}

	public boolean darDeBajaEmpleado(Empleado empleado) {
		boolean ret = true;
		empleado.setFechaDeBaja(LocalDate.now());
		empleado.setEstadoEmpleado(EstadoEmpleado.NOACTIVO);
		ret = ret && dao.saveOrUpdate(empleado.getPersona());
		ret = ret && this.delete(empleado.getPersona());
		if (!ret) {
			// Rollback.
			empleado.setFechaDeBaja(null);
			empleado.setEstadoEmpleado(EstadoEmpleado.ACTIVO);
			empleado.getPersona().setEstadoRegistro(EstadoRegistro.ACTIVO);
			dao.saveOrUpdate(empleado.getPersona());
		}

		return ret;

	}

	public boolean reincorporarEmpleado(Empleado empleado) {
		boolean ret = false;
		if (empleado != null) {
			if (empleado.getPersona() != null) {
				empleado.setEstadoEmpleado(EstadoEmpleado.ACTIVO);
				LocalDate fechaAlta = empleado.getFechaDeAlta();
				LocalDate fechaBaja = empleado.getFechaDeBaja();
				empleado.setFechaDeAlta(LocalDate.now());
				empleado.setFechaDeBaja(null);
				ret = dao.saveOrUpdate(empleado.getPersona());
				if (ret == false) {
					empleado.setEstadoEmpleado(EstadoEmpleado.NOACTIVO);
					empleado.setFechaDeAlta(fechaAlta);
					empleado.setFechaDeBaja(fechaBaja);}
			}
		}
		return ret;
	}

}
