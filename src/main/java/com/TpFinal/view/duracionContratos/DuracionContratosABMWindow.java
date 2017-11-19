package com.TpFinal.view.duracionContratos;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.TpFinal.dto.inmueble.Inmueble;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class DuracionContratosABMWindow extends Window {
    private DuracionContratosABMView abmView;

    public DuracionContratosABMWindow(String caption) {
	super(caption);
	abmView = new DuracionContratosABMView();
	setModal(true);
	setResizable(false);
	setClosable(true);
	setDraggable(false);
	setHeight(100.0f, Unit.PERCENTAGE);
	setWidth(100.0f, Unit.PERCENTAGE);
	center();
	VerticalLayout content = new VerticalLayout();
	content.setSizeFull();
	content.setMargin(new MarginInfo(true, true, true, true));
	content.addComponent(abmView);
	setContent(content);
	
	UI.getCurrent().addWindow(this);
	this.focus();
    }

}
