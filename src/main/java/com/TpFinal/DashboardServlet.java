package com.TpFinal;

import com.TpFinal.data.conexion.ConexionHibernate;
import com.TpFinal.services.Planificador;
import com.TpFinal.utils.GeneradorDeDatosSinAsociaciones;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionDestroyEvent;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinServlet;

import java.util.Locale;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class DashboardServlet extends VaadinServlet implements SessionInitListener, SessionDestroyListener {
    Logger logger = Logger.getLogger(DashboardServlet.class);

    @Override
    protected final void servletInitialized() throws ServletException {
	super.servletInitialized();
	getService().addSessionInitListener(new DashboardSessionInitListener());
	getService().addSessionInitListener(this);
	getService().addSessionDestroyListener(this);
	logger.info("===================");
	logger.info("Iniciando Server..");
	logger.info("===================");
	establecerConexionesBD();
	iniciarScheduller();
    }

    private void iniciarScheduller() {
	logger.info("======================");
	logger.info("Iniciando Scheduller..");
	logger.info("======================");
	Planificador planificador = Planificador.get();
	planificador.encender();
    }

    private void establecerConexionesBD() {
	logger.info("==============================================");
	logger.info("Estableciendo conexiones a la base de datos..");
	logger.info("==============================================");
	ConexionHibernate.createSessionFactory();
    }

    @Override
    public void sessionDestroy(SessionDestroyEvent event) {
	// TODO Auto-generated method stub

    }

    @Override
    public void sessionInit(SessionInitEvent event) throws ServiceException {
	logger.info("=================");
	logger.info("Generando datos..");
	logger.info("=================");
	GeneradorDeDatosSinAsociaciones.generarDatos(4);
	logger.info("===========================");
	logger.info("Iniciando Demo Scheduller..");
	logger.info("===========================");
	Planificador.initDemo();
	

    }

    @Override
    public void destroy() {
	super.destroy();
	logger.info("===================");
	logger.info("Deteniendo Server..");
	logger.info("===================");
	cerrarConexiones();
	apagarScheduller();
    }

    private void apagarScheduller() {
	logger.info("=======================");
	logger.info("Deteniendo Scheduller..");
	logger.info("=======================");
	Planificador planificador = Planificador.get();
	planificador.apagar();
    }

    private void cerrarConexiones() {
	logger.info("=========================================");
	logger.info("Cerrando conexiones a la base de datos..");
	logger.info("=========================================");
	ConexionHibernate.close();
    }
}