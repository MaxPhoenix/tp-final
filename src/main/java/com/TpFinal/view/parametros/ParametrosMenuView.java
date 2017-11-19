package com.TpFinal.view.parametros;

import java.math.BigDecimal;

import com.TpFinal.dto.parametrosSistema.ParametrosSistema;
import com.TpFinal.services.DashboardEvent;
import com.TpFinal.services.ParametrosSistemaService;
import com.TpFinal.view.component.BlueLabel;
import com.TpFinal.view.component.DefaultLayout;
import com.TpFinal.view.component.DialogConfirmacion;
import com.TpFinal.view.duracionContratos.DuracionContratosABMWindow;
import com.google.common.eventbus.Subscribe;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.shared.Position;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;

/* User Interface written in Java.
 *
 * Define the user interface shown on the Vaadin generated web page by extending the UI class.
 * By default, a new UI instance is automatically created when the page is loaded. To reuse
 * the same instance, add @PreserveOnRefresh.
 */

@Title("Addressbook")
@Theme("valo")
public class ParametrosMenuView extends DefaultLayout implements View {

    // Para identificar los layout de acciones
    private int acciones = 0;
    FormLayout mainLayout;
    private boolean isonMobile = false;
    private Button duracionesContratos = new Button("Duraciones de Contratos", VaadinIcons.CALENDAR_CLOCK);
    private Button sellados = new Button("Sellado de bancos", VaadinIcons.INSTITUTION);

    // Contratos:
    private BlueLabel seccionContratos = new BlueLabel("Parámetros de contratos");
    private TextField proximoAVencer = new TextField("Próximo a Vencer (dias)");
    private TextField diaDePago = new TextField("Día de pago");
    private TextField cantMininimaCertificados = new TextField("Cantidad mínima de certificados");
    private TextField valorCertificado = new TextField("Valor Certificado ($)");
    private TextField comisionAPropietario = new TextField("Comisión a propietario (%)");
    private TextField comisionAInquilino = new TextField("Comisión a inquilino (%)");
    private TextField comisionCobro = new TextField("Comisión cobro de cuota");
    // Recordatorios
    private BlueLabel seccionRecordatorios = new BlueLabel("Parámetros de recordatorios");
    private TextField frecuenciaAvisoA = new TextField("Frecuencia de aviso calificación A");
    private TextField frecuenciaAvisoB = new TextField("Frecuencia de aviso calificación B");
    private TextField frecuenciaAvisoC = new TextField("Frecuencia de aviso calificación C");
    private TextField frecuenciaAvisoD = new TextField("Frecuencia de aviso calificación D");
    private Button guardar = new Button("Guardar", VaadinIcons.CHECK);

    // Services y binder
    private Binder<ParametrosSistema> binder = new Binder<>();
    private ParametrosSistema parametros;

    // ContratoDuracionService service = new ContratoDuracionService();

    public ParametrosMenuView() {
	super();
	parametros = ParametrosSistemaService.getParametros();
	buildLayout();
	configureComponents();
	binding();
	binder.readBean(parametros);
    }

    private void binding() {
	binder.forField(this.cantMininimaCertificados)
		.asRequired("Campo requerido")
		.withConverter(new StringToIntegerConverter("Debe ingresar un número"))
		.withValidator(n -> n >= 0, "Debe ingresar un número no negativo")
		.bind(ParametrosSistema::getCantMinimaCertificados, ParametrosSistema::setCantMinimaCertificados);

	binder.forField(this.comisionAInquilino)
		.asRequired("Campo requerido")
		.withConverter(new StringToIntegerConverter("Debe ingresar un número"))
		.withValidator(n -> n >= 0, "Debe ingresar un número no negativo")
		.bind(ParametrosSistema::getComisionAInquilino, ParametrosSistema::setComisionAInquilino);

	binder.forField(this.comisionAPropietario)
		.asRequired("Campo requerido")
		.withConverter(new StringToIntegerConverter("Debe ingresar un número"))
		.withValidator(n -> n >= 0, "Debe ingresar un número no negativo")
		.bind(ParametrosSistema::getComisionAPropietario, ParametrosSistema::setComisionAPropietario);

	binder.forField(this.comisionCobro)
		.asRequired("Campo requerido")
		.withConverter(new StringToDoubleConverter("Debe ingresar un número"))
		.withValidator(n -> n >= 0, "Debe ingresar un número no negativo")
		.bind(ParametrosSistema::getComisionCobro, ParametrosSistema::setComisionCobro);

	binder.forField(this.diaDePago)
		.asRequired("Campo requerido")
		.withConverter(new StringToIntegerConverter("Debe ingresar un número"))
		.withValidator(n -> n > 0 && n <= 28, "Debe ingresar un número entre 1 y 28")
		.bind(ParametrosSistema::getDiaDePago, ParametrosSistema::setDiaDePago);

	binder.forField(this.frecuenciaAvisoA)
		.asRequired("Campo requerido")
		.withConverter(new StringToIntegerConverter("Debe ingresar un número"))
		.withValidator(n -> n > 0, "Debe ingresar un número positivo")
		.bind(ParametrosSistema::getFrecuenciaAvisoCategoriaA, ParametrosSistema::setFrecuenciaAvisoCategoriaA);

	binder.forField(this.frecuenciaAvisoB)
		.asRequired("Campo requerido")
		.withConverter(new StringToIntegerConverter("Debe ingresar un número"))
		.withValidator(n -> n > 0, "Debe ingresar un número positivo")
		.bind(ParametrosSistema::getFrecuenciaAvisoCategoriaB, ParametrosSistema::setFrecuenciaAvisoCategoriaB);

	binder.forField(this.frecuenciaAvisoC)
		.asRequired("Campo requerido")
		.withConverter(new StringToIntegerConverter("Debe ingresar un número"))
		.withValidator(n -> n > 0, "Debe ingresar un número positivo")
		.bind(ParametrosSistema::getFrecuenciaAvisoCategoriaC, ParametrosSistema::setFrecuenciaAvisoCategoriaC);

	binder.forField(this.frecuenciaAvisoD)
		.asRequired("Campo requerido")
		.withConverter(new StringToIntegerConverter("Debe ingresar un número"))
		.withValidator(n -> n > 0, "Debe ingresar un número positivo")
		.bind(ParametrosSistema::getFrecuenciaAvisoCategoriaD, ParametrosSistema::setFrecuenciaAvisoCategoriaD);

	binder.forField(this.proximoAVencer)
		.asRequired("Campo requerido")
		.withConverter(new StringToIntegerConverter("Debe ingresar un número"))
		.withValidator(n -> n >= 0, "Debe ingresar un número no negativo")
		.bind(ParametrosSistema::getProximoAVencer, ParametrosSistema::setProximoAVencer);

	binder.forField(this.valorCertificado)
		.asRequired("Campo requerido")
		.withConverter(new StringToBigDecimalConverter("Debe ingresar un número"))
		.withValidator(n -> n.compareTo(BigDecimal.ZERO) >= 0, "Debe ingresar un número no negativo")
		.bind(ParametrosSistema::getValorCertificado, ParametrosSistema::setValorCertificado);
    }

    private void configureComponents() {
	Responsive.makeResponsive(this);

	guardar.addClickListener(click -> {
	    DialogConfirmacion dialog = new DialogConfirmacion("Guardar Parámetros", VaadinIcons.COGS,
		    "¿Realmente desea modificar los parámetros del Sistema?", "200px", save());
	    dialog.addNoListener(cancel());
	});
	
	duracionesContratos.addClickListener(click ->{
	    new DuracionContratosABMWindow("Duraciones de Contratos");
	});

    }

    private ClickListener cancel() {
	return click -> {
	    showErrorNotification("No se realizaron cambios.");
	    binder.readBean(ParametrosSistemaService.getParametros());
	};
    }

    private ClickListener save() {
	return click -> {
	    if (binder.writeBeanIfValid(parametros)) {
		ParametrosSistemaService.updateParametros(parametros);
		showSuccessNotification("Parámetros del sistema modificados satisfactoriamente.");
	    } else {
		Notification.show("Errores de validación, revise los campos ingresados.",
			Notification.Type.WARNING_MESSAGE);
	    }
	};
    }

    private void buildLayout() {

	CssLayout toolbar = new CssLayout();
	HorizontalLayout hl = new HorizontalLayout();
	toolbar.addComponents(duracionesContratos, sellados);
	toolbar.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
	hl.addComponent(toolbar);
	buildToolbar("Parámetros del Sistema", hl);
	mainLayout = new FormLayout(seccionContratos,
		proximoAVencer,
		diaDePago,
		cantMininimaCertificados,
		valorCertificado,
		comisionAPropietario,
		comisionAInquilino,
		comisionCobro,
		seccionRecordatorios,
		frecuenciaAvisoA,
		frecuenciaAvisoB,
		frecuenciaAvisoC,
		frecuenciaAvisoD, guardar);
	mainLayout.setSpacing(true);
	mainLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
	mainLayout.forEach(component -> {
	    component.setWidth("500px");
	    component.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
	});
	guardar.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
	guardar.addStyleNames(ValoTheme.BUTTON_PRIMARY);
	mainLayout.setDefaultComponentAlignment(Alignment.TOP_CENTER);
	mainLayout.setWidthUndefined();
	addComponent(mainLayout);
	this.setComponentAlignment(mainLayout, Alignment.TOP_CENTER);
	this.setExpandRatio(mainLayout, 1);

    }

    public void showErrorNotification(String notification) {
	Notification success = new Notification(
		notification);
	success.setDelayMsec(4000);
	success.setStyleName("bar error small");
	success.setPosition(Position.BOTTOM_CENTER);
	success.show(Page.getCurrent());
    }

    public void showSuccessNotification(String notification) {
	Notification success = new Notification(
		notification);
	success.setDelayMsec(2000);
	success.setStyleName("bar success small");
	success.setPosition(Position.BOTTOM_CENTER);
	success.show(Page.getCurrent());
    }

    public boolean isIsonMobile() {
	return isonMobile;
    }

    @Override
    public void detach() {
	super.detach();
	// A new instance of TransactionsView is created every time it's
	// navigated to so we'll need to clean up references to it on detach.
	com.TpFinal.services.DashboardEventBus.unregister(this);
    }

    @Subscribe
    public void browserWindowResized(final DashboardEvent.BrowserResizeEvent event) {
	if (Page.getCurrent().getBrowserWindowWidth() < 800) {

	    isonMobile = true;
	} else {
	    isonMobile = false;

	}

    }

    @Override
    public void enter(final ViewChangeListener.ViewChangeEvent event) {
    }

}
