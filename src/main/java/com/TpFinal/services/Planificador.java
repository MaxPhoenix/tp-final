package com.TpFinal.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

import com.TpFinal.dto.cita.Cita;
import com.TpFinal.dto.cobro.Cobro;
import com.TpFinal.dto.contrato.ContratoAlquiler;
import com.TpFinal.dto.parametrosSistema.ParametrosSistema;
import com.TpFinal.dto.persona.Calificacion;

public class Planificador {

	Scheduler sc;
	Job notificacion;
	Job mailSender;
	// citas
	Integer horasAntesRecoradatorio1;
	Integer horasAntesRecoradatorio2;
	// cobros por vencer
	Integer perioricidadPorVencerA;
	Integer perioricidadPorVencerB;
	Integer perioricidadPorVencerC;
	Integer perioricidadPorVencerD;
	Integer diasAntesCobroPorVencer;
	// cobros vencidos
	LocalTime horaInicioCobrosVencidos;
	//Contratos por vencer
	Integer mesesAntesVencimientoContrato;
	Integer perioricidadEnDiasVencimientoContrato;

	private static Planificador instancia;
	public static boolean demoIniciado = false;

	public static Planificador get() {
		if (instancia == null) {
			try {
				instancia = new Planificador();
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}

		return instancia;
	}

	private Planificador() throws SchedulerException {
		if (sc == null)
			this.sc = StdSchedulerFactory.getDefaultScheduler();
		//parametros
//		horasAntesRecoradatorio1 = 1;
//		horasAntesRecoradatorio2 = 24;
//		horasAntesCobrosVencidos = 0;
//		horaInicioCobrosVencidos = LocalTime.of(19, 00, 00);
//		mesesAntesVencimientoContrato=1;
//		perioricidadEnDiasVencimientoContrato=1;
//		this.perioricidadPorVencerA=1;
//		this.perioricidadPorVencerB=3;
//		this.perioricidadPorVencerC=4;
//		this.perioricidadPorVencerD=5;
//		this.diasAntesCobroPorVencer=10;
		
		ParametrosSistema ps = ParametrosSistemaService.getParametros();
		
		horasAntesRecoradatorio1 = 1;
		horasAntesRecoradatorio2 = 24;
		horaInicioCobrosVencidos = LocalTime.of(19, 00, 00);
		mesesAntesVencimientoContrato=1;
		perioricidadEnDiasVencimientoContrato=1;
		this.perioricidadPorVencerA=ps.getFrecuenciaAvisoCategoriaA();
		this.perioricidadPorVencerB=ps.getFrecuenciaAvisoCategoriaB();
		this.perioricidadPorVencerC=ps.getFrecuenciaAvisoCategoriaC();
		this.perioricidadPorVencerD=ps.getFrecuenciaAvisoCategoriaD();
		this.diasAntesCobroPorVencer=10;
	}

	public void setNotificacion(Job notificacion) {
		this.notificacion = notificacion;
	}
	
	public void setMailSender(Job mailSender) {
		this.mailSender=mailSender;
	}

	public void encender() {
		try {
			if (!sc.isStarted())
				try {
					sc.start();
				} catch (SchedulerException e) {
					e.printStackTrace();
				}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public void apagar() {
		try {
			if (sc.isStarted())
				try {
					sc.shutdown();
				} catch (SchedulerException e) {
					e.printStackTrace();
				}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public void addJobCita(Cita cita) {
		if (cita.getId() != null) {
			agregarJobNotificacionCita(cita, horasAntesRecoradatorio1, 1);
			agregarJobNotificacionCita(cita, horasAntesRecoradatorio2, 2);
		} else
			throw new IllegalArgumentException("La cita debe estar persistida");
	}

	public void addJobCita(Cita cita, Integer hsAntesRecordatorio1, Integer hsAntesRecordatorio2) {
		if (cita.getId() != null) {
			agregarJobNotificacionCita(cita, hsAntesRecordatorio1, 1);
			agregarJobNotificacionCita(cita, hsAntesRecordatorio2, 2);
		} else
			throw new IllegalArgumentException("La cita debe estar persistida");
	}

	public boolean removeJobCita(Cita cita) {
		boolean ret = false;
		try {
			if (cita.getId() != null) {

				return sc.unscheduleJob(TriggerKey.triggerKey(cita.getTriggerKey() + "-1")) &&
						sc.unscheduleJob(TriggerKey.triggerKey(cita.getTriggerKey() + "-2"));
			} else
				throw new IllegalArgumentException("La cita debe estar persistida");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public void addJobsCobrosVencidos(ContratoAlquiler c) {
		c.getCobros().forEach(c1 -> this.addJobCobroVencido(c1));
		System.out.println("[INFO] Agregados jobs de cobros vencidos correctamente");
	}

	public void addJobCobroVencido(Cobro cobro) {
		if (cobro.getId() != null) {
			agregarJobNotificacionCobroVencido(cobro, 1);
			agregarJobMailCobroPorVencido(cobro, 2);
			System.out.println("[INFO] Agregados jobs de cobros vencidos correctamente");
		} else
			throw new IllegalArgumentException("El Cobro debe estar persistida");
	}

	public boolean removeJobCobroVencido(Cobro cobro) {
		boolean ret = true;;
		try {
			if (cobro.getId() != null) {
				ret= ret&& sc.unscheduleJob(TriggerKey.triggerKey(cobro.getTriggerKey() + "-1"));
				ret= ret&& sc.unscheduleJob(TriggerKey.triggerKey(cobro.getTriggerKey() + "-2"));
				System.out.println("[INFO] Eliminados jobs de cobros vencidos correctamente");
			} else
				throw new IllegalArgumentException("El Cobro debe estar persistida");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public void addJobsCobrosPorVencer(ContratoAlquiler ca){
		ca.getCobros().forEach(cob -> {
			if(cob.getFechaDeVencimiento().compareTo(LocalDate.now())>0){
				addJobCobroPorVencer(cob);
			}
		});
		System.out.println("[INFO] Agregados jobs de cobros por vencer correctamente");
	}
	
	public void addJobCobroPorVencer(Cobro cobro) {
		if (cobro.getId() != null) {
			agregarJobMailCobroPorVencer(cobro, diasAntesCobroPorVencer, 3);
			System.out.println("[INFO] Agregados jobs de cobros por vencer correctamente");
		} else
			throw new IllegalArgumentException("El Cobro debe estar persistida");
	}

	public boolean removeJobCobroPorVencer(Cobro cobro) {
		boolean ret = true;;
		try {
			if (cobro.getId() != null) {
				ret= ret&& sc.unscheduleJob(TriggerKey.triggerKey(cobro.getTriggerKey() + "-3"));
				System.out.println("[INFO] Eliminados jobs de cobros por vencer correctamente");
			} else
				throw new IllegalArgumentException("El Cobro debe estar persistida");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public void addJobAlquilerPorVencer(ContratoAlquiler contrato) {
		if(contrato.getId()!=null && tieneVencimientoFuturo(contrato)) {
			agregarJobMailAlquilerPorVencer(contrato, mesesAntesVencimientoContrato,1);
			agregarJobNotificacionAlquilerPorVencer(contrato, mesesAntesVencimientoContrato,2);
			System.out.println("[INFO] Alquiler por vencer agregado a quartz correctamente");
		}
	}
	
	public boolean removeJobAlquilerPorVencer(ContratoAlquiler contrato) {
		boolean ret = true;
		try {
			if (contrato.getId() != null) {

				ret=ret && sc.unscheduleJob(TriggerKey.triggerKey(contrato.getTriggerKey() + "-1"));
				ret=ret && sc.unscheduleJob(TriggerKey.triggerKey(contrato.getTriggerKey() + "-2"));
				System.out.println("[INFO] Alquiler por vencer borrado de quartz correctamente");
			} else
				throw new IllegalArgumentException("El contrato debe estar persistida");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public void addJobAlquilerVencido(ContratoAlquiler contrato) {
		if(contrato.getId()!=null) {
			agregarJobMailAlquilerVencido(contrato, 3);
			System.out.println("[INFO] Alquiler vencido agregado a quartz correctamente");
		}
	}
	
	public boolean removeJobAlquilerPorVencido(ContratoAlquiler contrato) {
		boolean ret = true;
		try {
			if (contrato.getId() != null) {
				ret=ret && sc.unscheduleJob(TriggerKey.triggerKey(contrato.getTriggerKey() + "-3"));
				System.out.println("[INFO] Alquiler vencido borrado de quartz correctamente");
			} else
				throw new IllegalArgumentException("El contrato debe estar persistida");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public void agregarJobNotificacionSistema(String titulo, String mensaje, String username, LocalDateTime fechaInicio,
			LocalDateTime fechaFin, String perioricidad, String id) {
		try {

			String horan = "0 " + fechaInicio.getMinute() + " " + fechaInicio.getHour();
			horan = horan + " "+perioricidad;
			horan = horan + " * ? *";
			JobDetail j1 = JobBuilder.newJob(notificacion.getClass())
					.usingJobData("mensaje", mensaje)
					.usingJobData("titulo", titulo)
					.usingJobData("idCita", id)
					.usingJobData("usuario", username)
					.build();
			j1.getKey();

			Date startDate = Date.from(fechaInicio.minusMinutes(1).atZone(ZoneId.systemDefault()).toInstant());
			Date endDate = Date.from(fechaFin.plusMinutes(1).atZone(ZoneId.systemDefault()).toInstant());

			Trigger t = TriggerBuilder.newTrigger().withIdentity(id)
					.startAt(startDate)
					.withSchedule(CronScheduleBuilder.cronSchedule(horan))
					.endAt(endDate)
					.build();
			sc.scheduleJob(j1, t);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	public void agregarJobMail(String encabezado, String mensaje, String destinatario, LocalDateTime fechaInicio,
			LocalDateTime fechaFin, String perioricidad, String id) {
		try {

			String horan = "0 " + fechaInicio.getMinute() + " " + fechaInicio.getHour();
			horan = horan + " "+perioricidad;
			horan = horan + " * ? *";
			JobDetail j1 = JobBuilder.newJob(mailSender.getClass())
					.usingJobData("mensaje", mensaje)
					.usingJobData("encabezado", encabezado)
					.usingJobData("destinatario", destinatario)
					.build();
			j1.getKey();

			Date startDate = Date.from(fechaInicio.minusMinutes(1).atZone(ZoneId.systemDefault()).toInstant());
			Date endDate = Date.from(fechaFin.plusMinutes(1).atZone(ZoneId.systemDefault()).toInstant());

			Trigger t = TriggerBuilder.newTrigger().withIdentity(id)
					.startAt(startDate)
					.withSchedule(CronScheduleBuilder.cronSchedule(horan))
					.endAt(endDate)
					.build();
			sc.scheduleJob(j1, t);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	private void agregarJobNotificacionCita(Cita c, Integer horas, Integer nro) {
		LocalDateTime fechaInicio = c.getFechaInicio();
		fechaInicio = fechaInicio.minusHours(horas);
		LocalDateTime fechaFin = c.getFechaInicio();
		String perioricidad = "1/1";
		String triggerKey = c.getTriggerKey()+"-"+nro.toString();
		String username = c.getEmpleado();
		agregarJobNotificacionSistema(c.getTitulo(), c.getMessage(), username, fechaInicio, fechaFin, perioricidad, triggerKey);
	}

	private void agregarJobNotificacionCobroVencido(Cobro c, Integer nro) {
		LocalDateTime fechaInicio = LocalDateTime.of(c.getFechaDeVencimiento(), LocalTime.now().plusMinutes(1));
		LocalDateTime fechaFin = LocalDateTime.of(c.getFechaDeVencimiento(), LocalTime.now().plusMinutes(10));
		String perioricidad = "1/1";
		String triggerKey = c.getTriggerKey()+"-"+nro.toString();
		String username = "broadcast";
		agregarJobNotificacionSistema(c.getTitulo(), c.getMessage(), username, fechaInicio, fechaFin, perioricidad, triggerKey);
	}
	
	private void agregarJobMailCobroPorVencido(Cobro c, Integer nro) {
		LocalDateTime fechaInicio = LocalDateTime.of(c.getFechaDeVencimiento().plusDays(1), LocalTime.now().plusMinutes(1));
		LocalDateTime fechaFin = fechaInicio.plusYears(150);
		String dia=String.valueOf(fechaInicio.getDayOfMonth());
		String perioricidad = dia+"/"+this.perioricidadSegunCalificacion(c);
		String triggerKey = c.getTriggerKey()+"-"+nro.toString();
		ContratoAlquiler ca= (ContratoAlquiler) c.getContrato();
		String titulo="Pago vencido";
		String texto="Señor: "+ca.getInquilinoContrato().getPersona().toString()+" recuerde que el pago de su alquiler vencio el dia: "
				+c.getFechaDeVencimiento()
			.format(new DateTimeFormatterBuilder().appendPattern("dd/MM/YYYY").toFormatter()).toString();
		String mail=ca.getInquilinoContrato().getPersona().getMail();
		agregarJobMail(titulo, texto, mail, fechaInicio, fechaFin, perioricidad, triggerKey);
	}
	
	private void agregarJobMailCobroPorVencer(Cobro c, Integer dias, Integer nro) {
		LocalDateTime fechaInicio = LocalDateTime.of(c.getFechaDeVencimiento(), LocalTime.now().plusMinutes(1));
		LocalDateTime fechaFin = LocalDateTime.of(c.getFechaDeVencimiento(), LocalTime.now().plusMinutes(1));
		fechaInicio = fechaInicio.minusDays(dias);
		fechaFin = fechaFin.minusDays(1);
		String dia=String.valueOf(fechaInicio.getDayOfMonth());
		String perioricidad = dia+"/"+this.perioricidadSegunCalificacion(c);
		String triggerKey = c.getTriggerKey()+"-"+nro.toString();
		ContratoAlquiler ca= (ContratoAlquiler) c.getContrato();
		String titulo="Pago proximo a vencer";
		String texto="Señor: "+ca.getInquilinoContrato().getPersona().toString()+" recuerde que el pago de su alquiler vence el dia: "
				+c.getFechaDeVencimiento()
			.format(new DateTimeFormatterBuilder().appendPattern("dd/MM/YYYY").toFormatter()).toString();
		String mail=ca.getInquilinoContrato().getPersona().getMail();
		agregarJobMail(titulo, texto, mail, fechaInicio, fechaFin, perioricidad, triggerKey);
	}
	
	private void agregarJobMailAlquilerPorVencer(ContratoAlquiler c, Integer meses, Integer key) {
		LocalDateTime fechaInicio = LocalDateTime.of(c.getFechaIngreso().plusMonths(c.getDuracionContrato().getDuracion()),
				LocalTime.now().plusMinutes(1));
		//para que pare un dia antes del vencimiento
		LocalDateTime fechaFin = LocalDateTime.of(c.getFechaIngreso().plusMonths(c.getDuracionContrato().getDuracion())
				.minusDays(1), LocalTime.now().plusMinutes(10));
		fechaInicio=fechaInicio.minusMonths(meses);
		String triggerKey = c.getTriggerKey()+"-"+key.toString();
		String dia=String.valueOf(fechaInicio.getDayOfMonth());
		String perioricidad=dia+"/"+this.perioricidadEnDiasVencimientoContrato.toString();
		agregarJobMail(c.getTitulo(), c.getMessage(), c.getInquilinoContrato().getPersona().getMail(), fechaInicio, fechaFin, perioricidad,triggerKey);
	}
	
	private void agregarJobNotificacionAlquilerPorVencer(ContratoAlquiler c, Integer meses, Integer key) {
		LocalDateTime fechaInicio = LocalDateTime.of(c.getFechaIngreso().plusMonths(c.getDuracionContrato().getDuracion()),
				LocalTime.now().plusMinutes(1));
		//para que pare un dia antes del vencimiento
		LocalDateTime fechaFin = LocalDateTime.of(c.getFechaIngreso().plusMonths(c.getDuracionContrato().getDuracion())
				.minusDays(1), LocalTime.now().plusMinutes(10));
		fechaInicio=fechaInicio.minusMonths(meses);
		String triggerKey = c.getTriggerKey()+"-"+key.toString();
		String dia=String.valueOf(fechaInicio.getDayOfMonth());
		String perioricidad=dia+"/"+this.perioricidadEnDiasVencimientoContrato.toString();
		String username = "broadcast";
		agregarJobNotificacionSistema(c.getTitulo(), c.getMessage(), username, fechaInicio, fechaFin, perioricidad,triggerKey);
	}
	
	private void agregarJobMailAlquilerVencido(ContratoAlquiler c, Integer key) {
		LocalDateTime fechaInicio = LocalDateTime.of(c.getFechaIngreso().plusMonths(c.getDuracionContrato().getDuracion()),
				LocalTime.now().plusMinutes(1));
		LocalDateTime fechaFin = LocalDateTime.of(c.getFechaIngreso().plusMonths(c.getDuracionContrato().getDuracion()), LocalTime.now().plusMinutes(10));
		String triggerKey = c.getTriggerKey()+"-"+key.toString();
		String perioricidad="1/1";
		String mensaje="El contrato de alquiler se vencio el dia: "+c.getFechaIngreso().plusMonths(c.getDuracionContrato().getDuracion())
				.format(new DateTimeFormatterBuilder().appendPattern("dd/MM/YYYY").toFormatter()).toString();
		agregarJobMail("Se vencio el contrato de alquiler",mensaje, c.getInquilinoContrato().getPersona().getMail(), fechaInicio, fechaFin, perioricidad,triggerKey);
	}

	public LocalTime getHoraInicioCobrosVencidos() {
		return horaInicioCobrosVencidos;
	}

	public void setHoraInicioCobrosVencidos(LocalTime horaInicioCobrosVencidos) {
		this.horaInicioCobrosVencidos = horaInicioCobrosVencidos;
	}
	
	private boolean tieneVencimientoFuturo(ContratoAlquiler ca) {
		return ca.getFechaIngreso().plusMonths(ca.getDuracionContrato().getDuracion())
				.compareTo(LocalDate.now())>0;
	}
	
	private Calificacion getCalificacionInquilino(Cobro c) {
		ContratoAlquiler ca=(ContratoAlquiler) c.getContrato();
		Calificacion ret=Calificacion.C;
		if(ca.getInquilinoContrato().getCalificacion()!=null)
			ret=ca.getInquilinoContrato().getCalificacion();
		return ret;
	}
	
	private String perioricidadSegunCalificacion(Cobro c) {
		Integer ret=0;
		Calificacion cal= getCalificacionInquilino(c);
		if(cal.equals(Calificacion.A)) {
			ret=this.perioricidadPorVencerA;
		}else if(cal.equals(Calificacion.B)) {
			ret=this.perioricidadPorVencerB;
		}else if(cal.equals(Calificacion.C)) {
			ret=this.perioricidadPorVencerC;
		}else if(cal.equals(Calificacion.D)) {
			ret=this.perioricidadPorVencerD;
		}
		return ret.toString();
	}

}