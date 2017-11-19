package com.TpFinal.data.conexion;

import org.apache.commons.io.FileExistsException;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import com.TpFinal.properties.Parametros;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.Properties;

public class ConexionHibernate {

	private static Logger logger = Logger.getLogger(ConexionHibernate.class);

	private static Configuration configuration = new Configuration();
	private static SessionFactory sf = null;
	private static DateTime startOfBackupMode;

	// TipoConexion Por Defecto

	private static TipoConexion tipoConexion = TipoConexion.H2Server;
	private static Conexion conexion = Conexion.getTipoConexionFrom(tipoConexion);

	private static boolean backupmode = false;

	public static boolean isBackupmode() {
		return backupmode;
	}

	public static void setTipoConexion(TipoConexion tipo) {
		tipoConexion = tipo;
		conexion = Conexion.getTipoConexionFrom(tipoConexion);
		try {
			conexion.setDbName(Parametros.getProperty(Parametros.DB_NAME));
		} catch (IllegalArgumentException | FileExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		conexion.setDbRelativePath("Files");
		Parametros.setProperty(Parametros.DB_PATH, conexion.getDbPath());
		Parametros.setProperty(Parametros.DB_NAME, conexion.getDbName());
	}

	public static Conexion getConexion() {
		return conexion;
	}

	public static void setConexion(Conexion conexion) {
		ConexionHibernate.conexion = conexion;
	}

	private static Configuration getConfiguration() {
		// La ultima property es para que no crashee el add lo saque de aca:
		// https://stackoverflow.com/questions/32968527/hibernate-sequence-doesnt-exist
		configuration
				.configure().setProperties(conexion.getProperties());
		return configuration;
	}

	private static ServiceRegistry getServiceRegistry(Configuration configuration) {
		ServiceRegistry serviceRegistry = null;
		try {

			serviceRegistry = new StandardServiceRegistryBuilder()
					.applySettings(configuration.getProperties())
					.configure()
					.build();

		} catch (Exception e) {
			System.err.println("Error al conectar: ");
			e.printStackTrace();

		}

		return serviceRegistry;
	}

	public static void enterBackupMode() {

		startOfBackupMode = DateTime.now();
		backupmode = true;
	}

	public static void leaveBackupMode() {
		startOfBackupMode = null;
		backupmode = false;
	}

	private static SessionFactory getSession() {

		if (sf == null) {
			try {
				sf = getConfiguration().buildSessionFactory(getServiceRegistry(getConfiguration()));
				System.out.println("Conexion exitosa a url: " + conexion.getProperties().getProperty(Environment.URL));
				System.out.println("----------------");
			} catch (Exception e) {
				System.err.println("Error al establecer la conexión.");
				e.printStackTrace();
			}
		}
		return sf;
	}

	public static Session openSession() {
		if (backupmode) {

			if (startOfBackupMode.plusHours(1).isBefore(DateTime.now())) {
				backupmode = false;
			}
			try {
				while (backupmode) {
					Thread.sleep(1000);
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return getSession().openSession();
	}

	public static void createSessionFactory() {
		getSession();
	}

	public static void close() {
		if (sf != null) {
			sf.close();
			sf = null;
		}
	}

	public static void refreshConnection() {
		close();
		sf = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug("Actualizando Conexion");
			conexion.setDbName(Parametros.getProperty(Parametros.DB_NAME));
			if (logger.isDebugEnabled())
				logger.debug("Nueva URL: " + conexion.getUrl());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getSession();
	}

}