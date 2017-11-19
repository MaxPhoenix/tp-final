package com.TpFinal.properties;

import java.io.*;
import java.time.Instant;
import java.util.Properties;

import com.TpFinal.services.Cipher;
import org.apache.commons.io.FileExistsException;

public class Parametros {
    public static final String MAPS_KEY = "mapsKey";
    public static final String GEO_KEY = "geoKey";
    public static final String STATIC_MAPS_KEY = "staticMapsKey";
    public static final String EMAIL_KEY = "emailKey";
    public static final String DB_PATH ="dbPath";
    public static final String DB_NAME = "dbName";
    public static final String PARAM_FILE = "Files" + File.separator + "inmobi.properties";

    private Parametros() {

    }

    public static boolean setProperty(String key, String value) {
	Properties p = new Properties();
	OutputStream output = null;
	checkAndCreateDefaultsIfMissing();
	try {
	    FileInputStream fis = new FileInputStream(PARAM_FILE);
	    p.load(fis);
	    output = new FileOutputStream(PARAM_FILE);
	    p.setProperty(key, value);
	    // save properties to project root folder
	    p.store(output, null);
	    return true;

	} catch (IOException io) {
	    io.printStackTrace();

	} finally {
	    if (output != null) {
		try {

		    output.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }

	}
	return false;
    }

    public static String getProperty(String key) throws IllegalArgumentException, FileExistsException {
	Properties p = new Properties();
	try {

	    File f = new File(PARAM_FILE);
	    if (!f.exists()) {
		System.err.println("El archivo no existe..." +
			"creando el archivo con configuraciones por defecto");
		checkAndCreateDefaultsIfMissing();
		if (!f.exists())
		    throw new IllegalArgumentException(
			    "Error Fatal al crear las Properties por defecto, problema de privilegios de escritura?");
		else {
		    getProperty(key);
		}

	    }
	    FileInputStream fis = new FileInputStream(PARAM_FILE);
	    p.load(fis);
	} catch (Exception e) {
	    System.err.println(PARAM_FILE + ":archivo no encontrado.");
	}

	if (p.getProperty(key) == null) {
	    throw new IllegalArgumentException("El parametro no existe");
	}
	// Si es una api key desencriptala
	if (key.equals(EMAIL_KEY) || key.equals(STATIC_MAPS_KEY) || key.equals(GEO_KEY) || key.equals(MAPS_KEY))
	    return Cipher.decrypt(p.getProperty(key));

	return p.getProperty(key);
    }

    private static boolean buildDefaultProperties() {

	Properties prop = new Properties();
	OutputStream output = null;

	try {
	    Long randomName=+Instant.now().toEpochMilli();
	    output = new FileOutputStream(PARAM_FILE);
	    prop.setProperty(EMAIL_KEY,
		    "NjRaCxUzGScrPjY1XjcPCzIsEVIZADkKMVsHDjssABUJCzpSDzowIiArJjQYBCEsNUwsHCcmEi0/MUIGPBwyPD8hJSFR");
	    prop.setProperty(STATIC_MAPS_KEY, "JDoODiAJNEUIMDETGikJKBo8A1FKOhcDBzIhEABFACsMEisdBiM8");
	    prop.setProperty(GEO_KEY, "JDoODiAJNjlcMiwSPR0NNjgAIzUVNSErOFgCPjBDMykzDz0ADlQw");
	    prop.setProperty(MAPS_KEY, "JDoODiAJNyADIQw7OQITK1IxHysmDFgsAkM1BBkxTF4qAw4KER1d");
	    prop.setProperty(DB_PATH, "Files");
	    prop.setProperty(DB_NAME, "inmobi_db");
	    

	    prop.store(output, null);
	    return true;

	} catch (IOException io) {
	    io.printStackTrace();
	    System.err.println("Error al escribir las Properties: \n" + io.toString());
	} finally {
	    if (output != null) {
		try {

		    output.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }

	}
	return false;

    }

    public static boolean restoreDefaults() {
	File dir = new File("File");
	boolean ret = true;
	ret = ret && dir.mkdir();
	File props = new File(PARAM_FILE);
	ret = ret && buildDefaultProperties();
	return ret;
    }

    private static boolean checkAndCreateDefaultsIfMissing() {
	File dir = new File("Files");
	boolean ret = true;
	if (!dir.exists())
	    ret = ret && dir.mkdir();
	File props = new File(PARAM_FILE);
	if (!props.exists())
	    ret = ret && buildDefaultProperties();

	return ret;

    }

}
