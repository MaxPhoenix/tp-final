package com.TpFinal.view.reportes;

import java.util.ArrayList;
import java.util.List;

public enum TipoReporteMovimientos {
    Mensual, Anual;

    
    TipoReporteMovimientos() {
    }

    @Override
    public String toString() {
	switch (this) {
	case Mensual:
	    return "Mensual";
	case Anual:
	    return "Anual";
	default:
	    return super.toString();

	}
    
    }

    public static List<TipoReporteMovimientos> toList() {
    TipoReporteMovimientos[] clases = TipoReporteMovimientos.values();
	List<TipoReporteMovimientos> ret = new ArrayList<>();
	for (TipoReporteMovimientos c : clases) {
	    ret.add(c);
	}
	return ret;
    }


}