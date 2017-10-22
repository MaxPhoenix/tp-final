package com.TpFinal.dto.publicacion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Max on 10/5/2017.
 */
public enum EstadoPublicacion { Activa,Terminada;

    public static List<EstadoPublicacion> toList() {
    	return Arrays.asList(EstadoPublicacion.Activa, EstadoPublicacion.Terminada);
    }
}
