package com.TpFinal.Integracion.dao;

import com.TpFinal.data.conexion.ConexionHibernate;
import com.TpFinal.data.conexion.TipoConexion;
import com.TpFinal.data.dao.DAOContratoVentaImpl;
import com.TpFinal.data.dao.DAOInmuebleImpl;
import com.TpFinal.data.dao.interfaces.DAOContratoVenta;
import com.TpFinal.data.dao.interfaces.DAOInmueble;
import com.TpFinal.dto.contrato.ContratoVenta;
import com.TpFinal.dto.inmueble.ClaseInmueble;
import com.TpFinal.dto.inmueble.Coordenada;
import com.TpFinal.dto.inmueble.Direccion;
import com.TpFinal.dto.inmueble.EstadoInmueble;
import com.TpFinal.dto.inmueble.Inmueble;
import com.TpFinal.dto.inmueble.TipoInmueble;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DAOContratoVentaIT {
    DAOContratoVenta dao;
    DAOInmueble daoInmuebles;
    List<ContratoVenta> contratos = new ArrayList<>();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
	ConexionHibernate.setTipoConexion(TipoConexion.H2Test);
    }

    @Before
    public void setUp() throws Exception {

		File dir = new File("Files");
		deleteDirectory(dir);
		dir.mkdir();
		dao = new DAOContratoVentaImpl();
		daoInmuebles=new DAOInmuebleImpl();
		contratos = dao.readAll();
		contratos.forEach(dao::delete);
		contratos.clear();
		daoInmuebles.readAll().forEach(daoInmuebles::delete);
    }

    @After
    public void tearDown() throws Exception {
		contratos = dao.readAll();
		desvincularInmuebleYContrato();
		contratos.forEach(dao::delete);
		deleteDirectory(new File("Files"));
    }
    
    private void desvincularInmuebleYContrato() {
    	daoInmuebles.readAll().forEach(inmueble ->{
    	    inmueble.setContratos(null);
    	    daoInmuebles.saveOrUpdate(inmueble);
    	});
    	dao.readAll().forEach(contrato -> {
    	    contrato.setInmueble(null);
    	    dao.saveOrUpdate(contrato);
    	});
    	
        }

    @Test
    public void agregarSinDocs() {
		dao.save(instancia("1.00"));
		dao.save(instancia("2.00"));
		dao.save(instancia("3.00"));
	
		assertEquals(3, dao.readAll().size());
		
    }

    @Test
    public void delete() {
		dao.save(instancia("1.00"));
		dao.save(instancia("2.00"));
		dao.save(instancia("3.00"));
	
		dao.delete(dao.readAll().get(0));
		assertEquals(dao.readAll().size(), 2);
	    }
	
	    @Test
	    public void update() {
		dao.save(instancia("1.00"));
		dao.save(instancia("2.00"));
		dao.save(instancia("3.00"));
	
		dao.readAll().forEach(contrato -> {
		    contrato.setPrecioVenta(new BigDecimal("10.00"));
		    dao.update(contrato);
		});
	
		dao.readAll().forEach(contrato -> {
		    assertEquals(new BigDecimal("10.00"), contrato.getPrecioVenta());
		});

    }

    @Test
    public void altaConDoc() throws SQLException, IOException {
		String pathOriginal = "Files" + File.separator + "demo1.pdf";
		File archivoOriginal = new File(pathOriginal);
		archivoOriginal.createNewFile(); // lo creo
		dao.saveOrUpdateContrato(instancia("1"), archivoOriginal);
		assertEquals(1, dao.readAll().size());
		ContratoVenta contratoPersistido = dao.readAll().get(0); // Lo traigo de DB
		String pathPersistido = "Files" + File.separator + "demo2.pdf";
		guardar(pathPersistido, blobToBytes(contratoPersistido.getArchivo().getDocumento())); // Lo escribo en disco de nuevo
		File archivoPersistido = new File(pathPersistido);
		assertTrue(archivoPersistido.exists());
		assertTrue(FileUtils.contentEquals(archivoOriginal, archivoPersistido));
    }

    @Test
    public void testRelacionInmueble() {
		DAOInmuebleImpl daoI = new DAOInmuebleImpl();
		Inmueble i = unInmuebleNoPublicado();
		ContratoVenta c = instancia("1");
	
		daoI.create(i);
		c.setInmueble(i);
		//i.addContrato(c);
		dao.saveOrUpdate(c);
		daoI.saveOrUpdate(i);
		
	
		assertEquals(1, daoI.readAll().get(0).getContratos().size());
		assertEquals(i, dao.readAll().get(0).getInmueble());
		daoI.readAll().forEach(daoI::delete);

    }

    private byte[] blobToBytes(Blob c) throws SQLException, IOException {
	Blob blob = c;
	return blob.getBytes(1, (int) blob.length());

    }

    private void guardar(String filePath, byte[] fileBytes) throws IOException {
	FileOutputStream outputStream = new FileOutputStream(filePath);
	outputStream.write(fileBytes);
	outputStream.close();
    }

    static public boolean deleteDirectory(File path) {
	if (path.exists()) {
	    File[] files = path.listFiles();
	    for (int i = 0; i < files.length; i++) {
		if (files[i].isDirectory()) {
		    deleteDirectory(files[i]);
		} else {
		    files[i].delete();
		}
	    }
	}
	return (path.delete());
    }

    private ContratoVenta instancia(String numero) {
	return new ContratoVenta.Builder()
		.setFechaIngreso(LocalDate.of(2017, 05, 12))
		.setPrecioVenta(new BigDecimal(numero))
	
		.build();
    }

    

    private Inmueble unInmuebleNoPublicado() {
	return new Inmueble.Builder()
		.setaEstrenar(true)
		.setCantidadAmbientes(2)
		.setCantidadCocheras(3)
		.setCantidadDormitorios(1)
		.setClaseInmueble(ClaseInmueble.Casa)
		.setConAireAcondicionado(true)
		.setConJardin(true).setConParilla(true).setConPileta(true)
		.setDireccion(
			new Direccion.Builder()
				.setCalle("Una calle")
				.setCodPostal("asd123")
				.setCoordenada(new Coordenada())
				.setLocalidad("una Localidad")
				.setNro(123)
				.setPais("Argentina")
				.setProvincia("Buenos Aires")
				.build())
		.setEstadoInmueble(EstadoInmueble.NoPublicado)
		.setSuperficieCubierta(200)
		.setSuperficieTotal(400)
		.setTipoInmueble(TipoInmueble.Vivienda)
		.build();
    }

}
