package com.TpFinal.view.calendario;

import com.TpFinal.dto.cita.Cita;
import com.TpFinal.dto.cita.TipoCita;
import com.TpFinal.dto.contrato.Contrato;
import com.TpFinal.dto.persona.Empleado;
import com.TpFinal.dto.persona.Persona;
import com.TpFinal.services.CitaService;
import com.TpFinal.services.Planificador;
import com.TpFinal.view.component.DeleteButton;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;
import javafx.scene.control.DatePicker;
import org.apache.lucene.document.DateTools;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
public abstract class CitaFormWindow extends Window {

    public static final String ID = "profilepreferenceswindow";


    /*
     * Fields for editing the User object are defined here as class members.
     * They are later bound to a FieldGroup by calling
     * fieldGroup.bindMemberFields(this). The Fields' values don't need to be
     * explicitly set, calling fieldGroup.setItemDataSource(user) synchronizes
     * the fields with the user object.
     */
    protected HorizontalLayout root = new HorizontalLayout();
    protected  Cita cita;
    private  Binder<Cita> binderCita = new Binder<>(Cita.class);
    protected CitaService service = new CitaService();
    private DateTimeField fechaInicio= new DateTimeField();
    private DateTimeField fechaFin= new DateTimeField();

    private TextField citado = new TextField("Citado");
    private TextField direccionLugar = new TextField("Dirección");
    private TextArea observaciones = new TextArea("Info");
    private ComboBox<TipoCita> tipocita= new ComboBox<>("Tipo");
    DeleteButton borrar = new DeleteButton("Eliminar",
            VaadinIcons.WARNING, "Eliminar", "20%", e -> delete());

    public CitaFormWindow(Cita p) {
        this.cita =p;
        configureComponents();
        binding();
        setCita(cita);
    }
    public abstract void onSave();
    private void configureComponents(){

        tipocita.setItems(Arrays.asList(TipoCita.values()));
        addStyleName("profile-window");
        setId(ID);
        Responsive.makeResponsive(this);

        setModal(true);
        setCloseShortcut(KeyCode.ESCAPE, null);
        setResizable(false);
        setClosable(true);
       setHeight(220.0f, Unit.PERCENTAGE);

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setMargin(new MarginInfo(true, false, false, false));
        content.setSpacing(false);
        setContent(content);

        TabSheet detailsWrapper = new TabSheet();
        detailsWrapper.setSizeFull();
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_ICONS_ON_TOP);
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_CENTERED_TABS);
        content.addComponent(detailsWrapper);
        content.setExpandRatio(detailsWrapper, 1f);

        detailsWrapper.addComponent(buildProfileTab());
        //  detailsWrapper.addComponent(buildPreferencesTab());

        content.addComponent(buildFooter());
        setWidth(400.0f, Unit.PIXELS);
        //cita.addStyleName("notifications");
        //cita.setResizable(false);

        center();
        getUI().getCurrent().addWindow(this);
        focus();


    }

    private void binding(){
        //binder.bindInstanceFields(this); //Binding automatico
        citado.setRequiredIndicatorVisible(true);
        direccionLugar.setRequiredIndicatorVisible(true);
        binderCita.forField(citado).asRequired("Ingrese un citado").
                bind(Cita::getCitado,Cita::setCitado);
        binderCita.forField(direccionLugar).asRequired("Ingrese un Lugar/Direccion").
                bind(Cita::getDireccionLugar,Cita::setDireccionLugar);
        binderCita.forField(observaciones).
                bind(Cita::getObservaciones,Cita::setObservaciones);
        binderCita.forField(fechaInicio).asRequired("Seleccione una fecha de inicio")
                .bind(Cita::getFechaInicio,Cita::setFechaInicio);
        binderCita.forField(fechaFin).asRequired("Seleccione una fecha de fin")
                .bind(Cita::getFechaFin,Cita::setFechaFin);
        binderCita.forField(tipocita).asRequired("Seleccione un tipo de Cita").
                bind(Cita::getTipoDeCita, Cita::setTipoDeCita);

    }

    private void setCita(Cita cita) {
        borrar.setVisible(cita.getId()!=null);
        this.cita = cita;
        binderCita.readBean(cita);
        // Show delete button for only Persons already in the database

        setVisible(true);
        citado.selectAll();


    }
    private void delete(){
        boolean success=false;
        try {

        	success=Planificador.get().removeCita(cita);
            success=service.delete(cita);



        } catch (Exception e) {
            Notification.show("Error al Borrar");
            e.printStackTrace();

            return;
        }


        if(success) {
            Notification exito = new Notification(
                    "Borrado: " + cita.getDetails());
            exito.setDelayMsec(2000);
            exito.setStyleName("bar success small");
            exito.setPosition(Position.BOTTOM_CENTER);
            exito.show(Page.getCurrent());
            close();
            onSave();

        }
        else{
            close();
            onSave();
            Notification.show("Error al borarr la cita");
        }

    }

    private Empleado getCurrentUser() {
        return (Empleado) VaadinSession.getCurrent()
                .getAttribute(Empleado.class.getName());
    }
    private void save(){
        boolean success=false;
        try {
            binderCita.writeBean(cita);
            cita.setEmpleado(getCurrentUser());
            service.saveOrUpdate(cita);
            Planificador.get().addCita(service.getUltimaAgregada());
            onSave();
            success=true;


        } catch (ValidationException e) {
            Notification.show("Error al guardar, por favor revise los campos e intente de nuevo");
            e.printStackTrace();
            System.out.println( e.getValidationErrors()+" "+e.getFieldValidationErrors());

            return;
        }


        if(success) {
            Notification exito = new Notification(
                    "Guardado: " + cita.getDetails());
            exito.setDelayMsec(2000);
            exito.setStyleName("bar success small");
            exito.setPosition(Position.BOTTOM_CENTER);
            exito.show(Page.getCurrent());
            close();
        }

    }
    private Component buildFooter() {

        HorizontalLayout footer = new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);
        footer.setSpacing(false);

        Button ok = new Button("Guardar");
        ok.addStyleName(ValoTheme.BUTTON_PRIMARY);
        ok.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                save();

            }
        });
        ok.focus();
        footer.addComponent(ok);

        footer.setComponentAlignment(ok, Alignment.TOP_RIGHT);
        return footer;
    }


    private Component buildProfileTab() {

        root.setCaption("Cita");
        root.setIcon(VaadinIcons.USER);
        root.setWidth(100.0f, Unit.PERCENTAGE);
        root.setHeight(500.0f, Unit.PERCENTAGE);
        root.setMargin(true);
        root.addStyleName("profile-form");

        FormLayout details = new FormLayout();
        details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        root.addComponent(details);
        root.setExpandRatio(details, 1);

        details.addComponent(citado);
        details.addComponent(direccionLugar);
        details.addComponent(fechaInicio);
        details.addComponent(fechaFin);
        details.addComponent(tipocita);
        fechaInicio.setCaption("Inicio");
        fechaFin.setCaption("Fin");



        Label section = new Label("Información Adicional");
        section.addStyleName(ValoTheme.LABEL_H4);
        section.addStyleName(ValoTheme.LABEL_COLORED);
        details.addComponent(section);
        observaciones.setWidth("100%");
        observaciones.setRows(4);
        details.addComponents(observaciones,borrar);
        details.setComponentAlignment(borrar,Alignment.TOP_CENTER);

        return root;
    }

}
