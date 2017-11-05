package com.TpFinal.view.inmuebles;

import com.TpFinal.dto.cita.Cita;
import com.TpFinal.dto.cita.TipoCita;
import com.TpFinal.dto.inmueble.Inmueble;
import com.TpFinal.services.CitaService;
import com.TpFinal.services.CredencialService;
import com.TpFinal.services.InmuebleService;
import com.TpFinal.view.component.DeleteButton;
import com.vaadin.data.*;
import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;

public abstract class ImagenesInmuebleWindow extends Window{
    public static final String ID = "profilepreferenceswindow";


    /*
     * Fields for editing the User object are defined here as class members.
     * They are later bound to a FieldGroup by calling
     * fieldGroup.bindMemberFields(this). The Fields' values don't need to be
     * explicitly set, calling fieldGroup.setItemDataSource(user) synchronizes
     * the fields with the user object.
     */
    protected HorizontalLayout root = new HorizontalLayout();
    private InmuebleService inmbService = new InmuebleService();
    protected Inmueble inmueble;

    DeleteButton borrar = new DeleteButton("Eliminar",
            VaadinIcons.WARNING, "Eliminar", "20%", e -> delete());

    // Create the selection component
    ListSelect<String> select = new ListSelect<>("Imagenes");
    private Image imagen;



    private void delete() {
    }

    public ImagenesInmuebleWindow (Inmueble inmueble) {
        this.inmueble =inmueble;
        configureComponents();
        binding();
        setInmueble(this.inmueble);

        // Add some items

        //select.setItems("Mercury", "Venus", "Earth");

// Show 5 items and a scrollbar if there are more
        select.setRows(5);

        select.addValueChangeListener(event -> {
            Set<String> selected = event.getValue();


        });
    }
    public abstract void onSave();
    private void configureComponents(){

        imagen = new Image(null, null);
        imagen.setWidth(100.0f, Unit.PIXELS);
        imagen.setCaption(null);
        imagen.setSource(inmbService.getPortada(inmueble));
        addStyleName("profile-window");
        setId(ID);
        Responsive.makeResponsive(this);

        setModal(true);
        setCloseShortcut(ShortcutAction.KeyCode.ESCAPE, null);
        setResizable(false);
        setClosable(true);
        setHeight(500f, Sizeable.Unit.PERCENTAGE);

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
        setWidth(400.0f, Sizeable.Unit.PIXELS);
        //inmueble.addStyleName("notifications");
        //inmueble.setResizable(false);

        center();
        getUI().getCurrent().addWindow(this);
        focus();
        this.addCloseListener(new Window.CloseListener() {
            @Override
            public void windowClose(Window.CloseEvent closeEvent) {
                onSave();
            }
        });


    }

    private void binding(){


    }



    private void setInmueble(Inmueble inmueble) {
        imagen.setSource(inmbService.getPortada(inmueble));



    }
    private Component buildFooter() {

        HorizontalLayout footer = new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        footer.setSpacing(false);

        Button ok = new Button("Guardar");
        ok.addStyleName(ValoTheme.BUTTON_PRIMARY);
        ok.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                close();

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
        root.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        //root.setHeight(500.0f, Unit.PERCENTAGE);
        root.setMargin(true);
        root.addStyleName("profile-form");

        FormLayout details = new FormLayout();
        details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        root.addComponent(details);
        root.setExpandRatio(details, 1);

        details.addComponents();
        details.setComponentAlignment(borrar,Alignment.TOP_CENTER);

        return root;
    }

}
