package com.TpFinal.Integracion.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.TpFinal.dto.EstadoRegistro;
import com.TpFinal.dto.cita.Cita;
import com.TpFinal.dto.cita.TipoCita;
import com.TpFinal.dto.cobro.Cobro;
import com.TpFinal.dto.contrato.ContratoAlquiler;
import com.TpFinal.dto.contrato.ContratoDuracion;
import com.TpFinal.dto.contrato.EstadoContrato;
import com.TpFinal.dto.contrato.TipoInteres;
import com.TpFinal.dto.interfaces.Messageable;
import com.TpFinal.dto.persona.CategoriaEmpleado;
import com.TpFinal.dto.persona.Credencial;
import com.TpFinal.dto.persona.Empleado;
import com.TpFinal.dto.persona.Inquilino;
import com.TpFinal.dto.persona.Persona;
import com.TpFinal.dto.persona.Rol;
import com.TpFinal.services.ContratoService;
import com.TpFinal.services.NotificadorConcreto;
import com.TpFinal.services.Planificador;
import com.itextpdf.text.log.SysoCounter;


public class PlanificadorIT {
	Planificador sc;

	@Before
	public void setUp() throws Exception {
		sc = Planificador.get();
		sc.encender();
	}

	@After
	public void tearDown() throws Exception {
		//sc.apagar();
	}

	@Test
	public void eliminarCita() {
		try {
			sc.setNotificacion(new NotificadorConcreto());
			List<Messageable>citas = new ArrayList<>();
			System.out.println("Agregando citas \n deberia eliminarlas");
			for(int i=0; i<3; i++) {
				LocalDateTime fInicio = LocalDateTime.now();
				fInicio=fInicio.plusMinutes(i+1);
				fInicio=fInicio.plusHours(24);

				Empleado e=instanciaEmpleado();
				e.setIdRol(new Long (i));

				Cita c = new Cita.Builder()
						.setCitado("Señor "+String.valueOf(i))
						.setDireccionLugar("sarasa: "+String.valueOf(i))
						.setFechahora(fInicio)
						.setObservaciones("obs"+String.valueOf(i))
						.setTipoDeCita(randomCita())
						.setEmpleado(e)
						.build();
				c.setId(Long.valueOf(i));
				citas.add(c);
			}
			sc.agregarJobs(citas);
			boolean eliminar=true;

			for (Messageable mess:citas){
				Cita citat=(Cita) mess;
				eliminar=eliminar&&sc.removeJobCita(citat);

			}
			Assert.assertTrue(eliminar);

			TimeUnit.SECONDS.sleep(5);
			System.out.println("Elimine todas");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println();
		System.out.println("--------------------");
		System.out.println();
	}
	
	@Test
	public void addCitas() {
		try {
			sc.setNotificacion(new NotificadorConcreto());
			List<Messageable>citas = new ArrayList<>();
			for(int i=0; i<3; i++) {
				LocalDateTime fInicio = LocalDateTime.now();
				fInicio=fInicio.plusMinutes(i+1);
				fInicio=fInicio.plusHours(1);

				Empleado e=instanciaEmpleado();
				e.setIdRol(new Long (i));

				Cita c = new Cita.Builder()
						.setCitado("Señor "+String.valueOf(i))
						.setDireccionLugar("sarasa: "+String.valueOf(i))
						.setFechahora(fInicio)
						.setObservaciones("obs"+String.valueOf(i))
						.setTipoDeCita(randomCita())
						.setEmpleado(e)
						.build();
				c.setId(Long.valueOf(i));
				citas.add(c);
			}
			sc.agregarJobs(citas);

			TimeUnit.SECONDS.sleep( 182);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println();
		System.out.println("--------------------");
		System.out.println();
	}
	
	@Test
	public void addCobros() throws InterruptedException {
		sc.setNotificacion(new NotificadorConcreto());
		sc.setHoraInicioCobrosVencidos(LocalTime.now().plusMinutes(2));
		ContratoAlquiler contrato=instanciaAlquilerSimple();
		contrato.setEstadoContrato(EstadoContrato.Vigente);
		contrato.setInquilinoContrato(personaInquilino(1).getInquilino());
		
		new ContratoService().addCobros(contrato);
		
		Long id=new Long(0);
		
		for (Messageable c: contrato.getCobros()) {
			Cobro c1=(Cobro)c;
			c1.SetId(id);
			id++;
		}
		
		contrato.getCobros().forEach(c -> Planificador.get().addJobCobroVencido(c));
		TimeUnit.SECONDS.sleep(62);
		System.out.println();
		System.out.println("--------------------");
		System.out.println();
	}

	public TipoCita randomCita() {
		Random r = new Random();
		int res=r.nextInt(4);
		TipoCita ret=null;
		if(res==0)
			ret=TipoCita.CelebContrato;
		else if(res==1)
			ret=TipoCita.ExhInmueble;
		else if(res==1)
			ret=TipoCita.Otros;
		else
			ret=TipoCita.Tasacion;
		return ret;
	}
	
	private Credencial instanciaCredencial() {
		Credencial c = new Credencial.Builder()
				.setUsuario("usuario")
				.setContrasenia("pass")
				.build();
		return c;
	}
	
	private Empleado instanciaEmpleado() {
		Empleado e = new Empleado.Builder()
				.setCategoriaEmpleado(CategoriaEmpleado.sinCategoria)
				.setCredencial(instanciaCredencial())
				.setFechaDeAlta(LocalDate.now())
				.build();
		return e;
	}
	
	private Persona personaInquilino(int i) {
		Persona ret= new Persona.Builder()
				.setApellido("dasd")
				.setNombre("dsad")
				.setDNI("231654")
				.setEsInmobiliaria(false)
				.setId(new Long(i))
				.setMail("dsda21d@sa.com")
				.setTelefono("132132")
				.build();
		ret.addRol(new Inquilino());
		return ret;
	}
	
    private ContratoAlquiler instanciaAlquilerSimple() {
    	LocalDate fecha=LocalDate.now();
    	fecha=fecha.minusMonths(5);
	ContratoAlquiler ret = new ContratoAlquiler.Builder()
		.setFechaIngreso(fecha)
		.setValorIncial(new BigDecimal("100.00"))
		.setDiaDePago(fecha.plusDays(10).getDayOfMonth())
		.setInteresPunitorio(new Double(50))
		.setIntervaloActualizacion(new Integer(2))
		.setTipoIncrementoCuota(TipoInteres.Simple)
		.setTipoInteresPunitorio(TipoInteres.Simple)
		.setPorcentajeIncremento(new Double(50))
		.setInquilinoContrato(null)
		.setDuracionContrato(instanciaContratoDuracion24())
		.setEstadoRegistro(EstadoRegistro.ACTIVO)

		.build();
	ret.setEstadoContrato(EstadoContrato.Vigente);
	return ret;
    }
    
    private ContratoDuracion instanciaContratoDuracion24() {
    	return new ContratoDuracion.Builder().setDescripcion("24 Horas").setDuracion(24).build();
        }
}