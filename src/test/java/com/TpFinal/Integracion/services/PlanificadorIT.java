package com.TpFinal.Integracion.services;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import com.TpFinal.services.Notificable;
import com.TpFinal.services.NotificadorConcreto;
import com.TpFinal.services.Planificador;


public class PlanificadorIT {
	Planificador sc;

	@Before
	public void setUp() throws Exception {
		sc = new Planificador();
		sc.encender();
	}

	@After
	public void tearDown() throws Exception {
		sc.apagar();
	}
	
	@Ignore
	@Test
	public void test() {
		sc.setNotificacion(new NotificadorConcreto());
		for(int i=0; i<3; i++) {
			LocalDate fInicio = LocalDate.now();
			LocalDate fFin = fInicio.plusDays(1);

			sc.agregarAccion("Mensaje numero "+i, fInicio, fFin, "15", String.valueOf(31+i), "1", Long.valueOf(i));
		}

		try {
			TimeUnit.SECONDS.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Ignore
	@Test
	public void testJob() {
		try {
			sc.setNotificacion(new NotificadorConcreto());
			for(int i=0; i<3; i++) {
				LocalDateTime fInicio = LocalDateTime.now();
				fInicio=fInicio.plusMinutes(i+1);
				LocalDateTime fFin = fInicio.plusDays(i+1);

				sc.agregarJob("m "+i, fInicio, fFin, "1", Long.valueOf(i));
			}


			TimeUnit.SECONDS.sleep(300);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}