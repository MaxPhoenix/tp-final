package com.TpFinal.services;

import com.TpFinal.data.dao.DAOImpl;
import com.TpFinal.dto.parametrosSistema.ParametrosSistema;

public class ParametrosSistemaService {
    private final static DAOImpl<ParametrosSistema> dao = new DAOImpl<>(ParametrosSistema.class);

    public ParametrosSistemaService() {
	// TODO Auto-generated constructor stub
    }

    
    /**
     *  Crea los parametros por defecto del sistema si no se encuentran guardados
     *  en la bd. Caso contrario no hace nada.
     */
    public static void crearParametrosDefault() {
	if (dao.readAll().isEmpty())
	    dao.save(new ParametrosSistema());
    }
    
    public static void updateParametros(ParametrosSistema parametros) {
	dao.merge(parametros);
    }
    
    public static ParametrosSistema getParametros() { 
	if (dao.readAll().isEmpty()) {
	    crearParametrosDefault();
	}
	return dao.readAll().get(0);
    }

}
