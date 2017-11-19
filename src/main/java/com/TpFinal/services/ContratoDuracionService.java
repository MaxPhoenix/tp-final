package com.TpFinal.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.TpFinal.data.dao.DAOContratoDuracionImpl;
import com.TpFinal.data.dao.interfaces.DAOContratoDuracion;
import com.TpFinal.dto.contrato.ContratoDuracion;
import com.TpFinal.view.duracionContratos.FiltroDuracion;

public class ContratoDuracionService {
    DAOContratoDuracion dao;

    public ContratoDuracionService() {
	dao = new DAOContratoDuracionImpl();
    }

    public boolean saveOrUpdate(ContratoDuracion p) {
	return dao.saveOrUpdate(p);
    }

    public boolean delete(ContratoDuracion p) {
	return dao.logicalDelete(p);
    }

    public List<ContratoDuracion> readAll() {
	return dao.readAllActives();
    }

    public List<ContratoDuracion> findAll(FiltroDuracion filtro) {
	List<ContratoDuracion> duraciones = dao.readAllActives().stream()
		.filter(filtro.getFiltroCompuesto())
		.collect(Collectors.toList());
	duraciones.sort(Comparator.comparing(ContratoDuracion::getId));
	return duraciones;
    }

    public static ContratoDuracion getInstancia() {
	return new ContratoDuracion.Builder()
		.setDescripcion("")
		.setDuracion(1)
		.build();
    }

    /**
     * Crea duraciones de 24 y 36 meses por defecto si la bd no tiene registros de duraciones activos.
     */
    public static void crearDuracionesPorDefecto() {
	DAOContratoDuracion dao = new DAOContratoDuracionImpl();
	if (dao.readAllActives().isEmpty()) {
	    dao.save(new ContratoDuracion.Builder().setDescripcion("24 Meses").setDuracion(24).build());
	    dao.save(new ContratoDuracion.Builder().setDescripcion("36 Meses").setDuracion(36).build());
	}
    }

}
