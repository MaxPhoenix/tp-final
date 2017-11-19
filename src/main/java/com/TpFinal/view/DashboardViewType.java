package com.TpFinal.view;

import com.TpFinal.view.cobros.CobrosABMView;
import com.TpFinal.view.contrato.ContratoABMView;
import com.TpFinal.view.duracionContratos.DuracionContratosABMView;
import com.TpFinal.view.empleados.EmpleadoABMView;
import com.TpFinal.view.inmuebles.InmuebleABMView;
import com.TpFinal.view.movimientos.MovimientoABMView;
import com.TpFinal.view.persona.PersonaABMView;
import com.TpFinal.view.publicacion.PublicacionABMView;
import com.TpFinal.view.reportes.ReportesView;
import com.TpFinal.view.parametros.ParametrosMenuView;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.server.Resource;

public enum DashboardViewType {
    INICIO("inicio", DashboardView.class, VaadinIcons.HOME, true),
	PERSONAS("clientes", PersonaABMView.class, VaadinIcons.USER, false),
	INMUEBLES("inmuebles", InmuebleABMView.class, VaadinIcons.BUILDING, false),
	PUBLICACIONES("publicaciones", PublicacionABMView.class, VaadinIcons.BOOKMARK_O, false),
	CONTRATOS("contratos", ContratoABMView.class, VaadinIcons.HANDSHAKE, false),
	COBROS("cobros", CobrosABMView.class, VaadinIcons.CASH, false),
	REPORTES("reportes", ReportesView.class, VaadinIcons.FILE, false),
	EMPLEADOS("empleados", EmpleadoABMView.class, VaadinIcons.GROUP, false),
	MOVIMIENTOS("movimientos", MovimientoABMView.class, VaadinIcons.CHART_GRID, false),
	PARAMETROS("parametros", ParametrosMenuView.class, VaadinIcons.COGS, false);

    private final String viewName;
    private final Class<? extends View> viewClass;
    private final Resource icon;
    private final boolean stateful;

    private DashboardViewType(final String viewName,
	    final Class<? extends View> viewClass, final Resource icon,
	    final boolean stateful) {
	this.viewName = viewName;
	this.viewClass = viewClass;
	this.icon = icon;
	this.stateful = stateful;

    }

    public boolean isStateful() {
	return stateful;
    }

    public String getViewName() {
	return viewName;
    }

    public Class<? extends View> getViewClass() {
	return viewClass;
    }

    public Resource getIcon() {
	return icon;
    }

    public static DashboardViewType getByViewName(final String viewName) {
	DashboardViewType result = null;
	for (DashboardViewType viewType : values()) {
	    if (viewType.getViewName().equals(viewName)) {
		result = viewType;
		break;
	    }
	}
	return result;
    }

}
