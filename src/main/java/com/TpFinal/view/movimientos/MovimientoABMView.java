package com.TpFinal.view.movimientos;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.TpFinal.dto.inmueble.TipoMoneda;
import com.TpFinal.dto.movimiento.Movimiento;
import com.TpFinal.services.DashboardEvent;
import com.TpFinal.services.MovimientoService;
import com.TpFinal.view.component.DefaultLayout;
import com.TpFinal.view.component.DialogConfirmacion;
import com.google.common.eventbus.Subscribe;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.shared.Position;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;

@Title("Addressbook")
@Theme("valo")
public class MovimientoABMView extends DefaultLayout implements View {

	/*
	 * Hundreds of widgets. Vaadin's user interface components are just Java objects
	 * that encapsulate and handle cross-browser support and client-server
	 * communication. The default Vaadin components are in the com.vaadin.ui package
	 * and there are over 500 more in vaadin.com/directory.
	 */

	// Para identificar los layout de acciones
	private int acciones = 0;
	private Grid<Movimiento> grid = new Grid<>();
	Button newItem = new Button("Nuevo");
	Button clearFilterTextBtn = new Button(VaadinIcons.CLOSE);
	//RadioButtonGroup<String> filtroRoles = new RadioButtonGroup<>();

	HorizontalLayout mainLayout;
	MovimientoForm finanzasForm = new MovimientoForm(this);
	private boolean isonMobile = false;

	MovimientoService service = new MovimientoService();

	//private FiltroDuracion filtro;

	public MovimientoABMView() {

		super();
		buildLayout();
		configureComponents();

	}

	private void configureComponents() {
		//filtro = new FiltroDuracion();
		clearFilterTextBtn.addClickListener(e -> ClearFilterBtnAction());

		newItem.addClickListener(e -> {

			finanzasForm.clearFields();
			grid.asSingleSelect().clear();
			finanzasForm.setMovimiento(null);
		});

		configureGrid();

		Responsive.makeResponsive(this);

		// grid.setSelectionMode(Grid.SelectionMod
		//
		// e.SINGLE);

		newItem.setStyleName(ValoTheme.BUTTON_PRIMARY);

		// filter.setIcon(VaadinIcons.SEARCH);
		// filter.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);

		updateList();
	}

	private void configureGrid() {

		grid.addComponentColumn(configurarAcciones()).setCaption("Acciones").setId("acciones");

		grid.addColumn(mov -> {
			String ret="";
			if( mov.getDescripcionMovimiento()!=null)
				ret=mov.getDescripcionMovimiento();
			return ret;
		}).setCaption("Descripcion").setId("descripcionMovimiento");

		grid.addColumn(mov -> {
			String ret = "";
			ret = TipoMoneda.getSimbolo(mov.getTipoMoneda()) + " " + mov.getMonto()
			.toString();
			return ret;
		}).setCaption("Monto").setId("monto");

		grid.addColumn(mov -> {
			String ret="";
			DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d/MM/uuuu");
			ret = mov.getFecha().format(formatters);
			return ret;
		}).setCaption("Fecha").setId("fecha");;

		grid.addColumn(mov -> {
			String ret="";
			ret=mov.getTipoMovimiento().toString();
			return ret;
		}).setCaption("Concepto").setId("tipoMovimiento");;

		grid.addColumn(mov -> {
			String ret="";
			ret=mov.getClaseMovimiento().toString();
			return ret;
		}).setCaption("Rubro").setId("claseMovimiento");;

		grid.setColumnOrder("acciones", "descripcionMovimiento", "monto","fecha","tipoMovimiento","claseMovimiento");
		grid.getColumns().forEach(col -> {
			col.setResizable(false);
			col.setHidable(true);
		});
		//HeaderRow filterRow = grid.appendHeaderRow();
		//filterRow.getCell("descripcion").setComponent(filtroDescripcion());
		//filterRow.getCell("duracion").setComponent(filtroDuracion());

	}

	/*
    private Component filtroDescripcion() {
	TextField filtroDescripcion = new TextField();
	filtroDescripcion.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
	filtroDescripcion.setPlaceholder("Sin Filtro");
	filtroDescripcion.addValueChangeListener(e -> {
	    if (e.getValue() != null) {
		if (!filtroDescripcion.isEmpty()) {
		    filtro.setFiltroDescripcion(duracion -> {
			if (duracion.getDescripcion() != null)
			    return duracion.getDescripcion().toLowerCase().contains(e.getValue().toLowerCase());
			return true;
		    });
		} else
		    filtro.setFiltroDescripcion(duracion -> true);

	    } else {
		filtro.setFiltroDescripcion(duracion -> true);
	    }
	    updateList();
	});
	return filtroDescripcion;
    }

	 */

	/*
    private Component filtroDuracion() {
	TextField filtroDuracion = new TextField();
	filtroDuracion.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
	filtroDuracion.setPlaceholder("Sin Filtro");
	filtroDuracion.addValueChangeListener(e -> {
	    if (e.getValue() != null) {
		if (!filtroDuracion.isEmpty()) {
		    filtro.setFiltroDuracion(duracion -> {
			if (duracion.getDuracion() != null)
			    return duracion.getDuracion().toString().toLowerCase().contains(e.getValue().toLowerCase());
			return true;
		    });
		} else
		    filtro.setFiltroDuracion(duracion -> true);

	    } else {
		filtro.setFiltroDuracion(duracion -> true);
	    }
	    updateList();
	});
	return filtroDuracion;
    }

	 */

	private ValueProvider<Movimiento, HorizontalLayout> configurarAcciones() {

		return movimiento -> {

			// Button edit = new Button(VaadinIcons.EDIT);
			// edit.addStyleNames(ValoTheme.BUTTON_QUIET, ValoTheme.BUTTON_SMALL);
			// edit.addClickListener(e -> {
			// DuracionContratosForm.setContratoDuracion(contratoduracion);
			// });
			// edit.setDescription("Editar");

			Button del = new Button(VaadinIcons.TRASH);
			del.addClickListener(click -> {
				DialogConfirmacion dialog = new DialogConfirmacion("Eliminar",
						VaadinIcons.WARNING,
						"¿Esta seguro que desea Eliminar?",
						"100px",
						confirmacion -> {
							service.delete(movimiento);
							showSuccessNotification("Movimiento eliminado: " + movimiento
									.getDescripcionMovimiento());
							updateList();
						});
			});

			del.addStyleNames(ValoTheme.BUTTON_QUIET, ValoTheme.BUTTON_SMALL);
			del.setDescription("Borrar");

			HorizontalLayout hl = new HorizontalLayout(del);
			hl.setSpacing(false);
			hl.setCaption("Accion " + acciones);
			acciones++;
			return hl;
		};
	}
	/*
	 * Robust layouts.
	 *
	 * Layouts are components that contain other components. HorizontalLayout
	 * contains TextField and Button. It is wrapped with a Grid into VerticalLayout
	 * for the left side of the screen. Allow user to resize the components with a
	 * SplitPanel.
	 *
	 * In addition to programmatically building layout in Java, you may also choose
	 * to setup layout declaratively with Vaadin Designer, CSS and HTML.
	 */

	public void setComponentsVisible(boolean b) {
		newItem.setVisible(b);
		// seleccionFiltro.setVisible(b);
		// clearFilterTextBtn.setVisible(b);
		if (isonMobile)
			grid.setVisible(b);

	}

	private void buildLayout() {

		CssLayout filtering = new CssLayout();
		HorizontalLayout hl = new HorizontalLayout();
		filtering.addComponents(clearFilterTextBtn, newItem);
		filtering.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		hl.addComponent(filtering);

		buildToolbar("Movimientos", hl);
		grid.setSizeFull();
		mainLayout = new HorizontalLayout(grid, finanzasForm);
		mainLayout.setSizeFull();
		addComponent(mainLayout);
		this.setExpandRatio(mainLayout, 1);

	}

	/*
	 * Choose the design patterns you like.
	 *
	 * It is good practice to have separate data access methods that handle the
	 * back-end access and/or the user interface updates. You can further split your
	 * code into classes to easier maintenance. With Vaadin you can follow MVC, MVP
	 * or any other design pattern you choose.
	 */

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

	public void updateList() {

		List<Movimiento> customers = service.readAll();
		//List<Movimiento> customers = service.findAll(filtro);
		grid.setItems(customers);
	}

	public boolean isIsonMobile() {
		return isonMobile;
	}

	public void ClearFilterBtnAction() {
		if (this.finanzasForm.isVisible()) {
			newItem.focus();
			finanzasForm.cancel();
		}
	}

	/*
	 * 
	 * Deployed as a Servlet or Portlet.
	 *
	 * You can specify additional servlet parameters like the URI and UI class name
	 * and turn on production mode when you have finished developing the
	 * application.
	 */
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