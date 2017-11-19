package com.TpFinal.view.movimientos;

import java.math.BigDecimal;

import com.TpFinal.dto.inmueble.TipoMoneda;
import com.TpFinal.dto.movimiento.ClaseMovimiento;
import com.TpFinal.dto.movimiento.Movimiento;
import com.TpFinal.dto.movimiento.TipoMovimiento;
import com.TpFinal.services.MovimientoService;
import com.TpFinal.view.component.DeleteButton;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

public class MovimientoForm extends FormLayout {
	 
		private Movimiento movimiento;
	    Button save = new Button("Guardar");
	    DeleteButton delete = new DeleteButton("Eliminar",
	            VaadinIcons.WARNING,"Eliminar","20%", new Button.ClickListener() {
	        @Override
	        public void buttonClick(Button.ClickEvent clickEvent) {
	            delete();
	        }
	    });


	    TextField descripcion = new TextField("Descripcion"); 
	    TextField monto = new TextField("Cantidad");
	    ComboBox <TipoMovimiento> comboTipoMov = new ComboBox <>("Tipo de Movimiento");
	    ComboBox <ClaseMovimiento> comboClaseMov = new ComboBox <>("Clase de Movimiento");
	    DateField fechaCarga = new DateField("Fecha de Movimiento");
	    RadioButtonGroup<TipoMoneda> rbgTipoMoneda = new RadioButtonGroup<>("Tipo Moneda", TipoMoneda.toList());
	    //FiltroDuracion filtro = new FiltroDuracion();

	    MovimientoService service = new MovimientoService();
	    private MovimientoABMView addressbookView;
	    private Binder<Movimiento> binderMovimiento= new Binder<>(Movimiento.class);
	    TabSheet tabSheet;

	    // Easily binding forms to beans and manage validation and buffering


	    public MovimientoForm(MovimientoABMView addressbook) {
	        // setSizeUndefined();

	        addressbookView=addressbook;
	        configureComponents();
	        binding();
	        buildLayout();

	    }

	    private void configureComponents() {
	      
	        delete.setStyleName(ValoTheme.BUTTON_DANGER);
	        save.addClickListener(e -> this.save());
	        //test.addClickListener(e -> this.test());

	        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
	        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
	        setVisible(false);
	        
	        comboTipoMov.setItems(TipoMovimiento.toList());
	        comboClaseMov.setItems(ClaseMovimiento.getValoresDisponiblesManual());
	    }


	    private void binding(){
	     
	        descripcion.setRequiredIndicatorVisible(true);
	        monto.setRequiredIndicatorVisible(true);
	        
	        binderMovimiento.forField(descripcion).asRequired("Debe ingresar una descripción").bind(Movimiento::getDescripcionMovimiento,Movimiento::setDescripcionMovimiento);
	                
	        binderMovimiento.forField(this.monto).withNullRepresentation("")
			.withConverter(new StringToBigDecimalConverter("Debe ingresar un monto")).withValidator(n -> {
			    return (n.compareTo(BigDecimal.ZERO) > 0);
			}, "Debe Ingresar un Valor Positivo").bind(Movimiento::getMonto, Movimiento::setMonto);
	        
	        binderMovimiento.forField(this.fechaCarga).asRequired("Seleccione una fecha de carga")
			.bind(Movimiento::getFecha, Movimiento::setFecha);
	        
	        binderMovimiento.forField(this.comboTipoMov).asRequired("Seleccione un tipo de movimiento")
			.bind(Movimiento::getTipoMovimiento, Movimiento::setTipoMovimiento);
	        
	        binderMovimiento.forField(this.comboClaseMov).asRequired("Seleccione una clase de movimiento")
			.bind(Movimiento::getClaseMovimiento, Movimiento::setClaseMovimiento);
	        
	        binderMovimiento.forField(this.rbgTipoMoneda).asRequired("Seleccione un tipo de moneda")
	        .bind(Movimiento::getTipoMoneda, Movimiento::setTipoMoneda);
	       
	    }

	    private void buildLayout() {
	        setSizeFull();
	        setMargin(true);

	        tabSheet=new TabSheet();

	        FormLayout principal=new FormLayout(comboTipoMov, comboClaseMov, descripcion, monto, fechaCarga, rbgTipoMoneda);
	        
	       
	        principal.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
	        
	        tabSheet.addTab(principal,"Principal");
	        
	        addComponent(tabSheet);
	        //HorizontalLayout actions = new HorizontalLayout(save,test,delete);
	        HorizontalLayout actions = new HorizontalLayout(save,delete);
	        addComponent(actions);
	        this.setSpacing(false);
	        actions.setSpacing(true);

	      //  addStyleName("v-scrollable");

	    }


	    public void setMovimiento(Movimiento movimiento) {
	   
	    	if(movimiento != null) {
	    		this.movimiento = movimiento;
	    		binderMovimiento.readBean(this.movimiento);
	    		delete.setVisible(true);
	    		}else {
	    		    this.movimiento = MovimientoService.getInstanciaOtro();
	    			delete.setVisible(false);
	    		}
	    		setVisible(true);
	    		getAddressbookView().setComponentsVisible(false);
	    		if (getAddressbookView().isIsonMobile())
	    		    this.focus();
	  
	    }

	    private void delete() {
	        
	    	service.delete(movimiento);
	        addressbookView.updateList();
	        setVisible(false);
	        getAddressbookView().setComponentsVisible(true);
	        getAddressbookView().showSuccessNotification("Borrado: "+ movimiento.getDescripcionMovimiento());

	    }

	    
	    private void save() {

	        boolean success=false;
	        try {
	        	binderMovimiento.writeBean(movimiento);
	            
	        	service.saveOrUpdate(movimiento);
	            success=true;


	        } catch (ValidationException e) {
	            e.printStackTrace();
	            Notification.show("Error al guardar, por favor revise los campos e intente de nuevo");
	            // Notification.show("Error: "+e.getCause());
	            return;
	        }
	        catch (Exception e){
	            e.printStackTrace();
	            Notification.show("Error: "+e.toString());
	        }

	        addressbookView.updateList();
	       /* String msg = String.format("Guardado '%s %s'.", persona.getNombre(),
	                persona.getApellido());*
	        Notification.show(msg, Type.TRAY_NOTIFICATION);*/
	        setVisible(false);
	        getAddressbookView().setComponentsVisible(true);


	        if(success)
	            getAddressbookView().showSuccessNotification("Guardado: "+ movimiento.getDescripcionMovimiento());


	    }

	    public void cancel() {
	        addressbookView.updateList();
	        setVisible(false);
	        getAddressbookView().setComponentsVisible(true);
	    }
	    
	    public void clearFields() {
	    	descripcion.clear();
	    	monto.clear();
	    }
	    
	    public MovimientoABMView getAddressbookView() {
	        return addressbookView;
	    }

	}
