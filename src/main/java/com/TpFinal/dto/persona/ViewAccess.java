package com.TpFinal.dto.persona;

import com.TpFinal.view.DashboardViewType;

import javax.swing.text.View;
import java.util.ArrayList;
import java.util.Arrays;

public enum ViewAccess {

    Admin(getAdminViews()),Agente(getAgenteViews()),Default(getDefaultViews()),Recovery(getRecoveryViews());
    public ArrayList<DashboardViewType> views;

   private ViewAccess(ArrayList<DashboardViewType> dato){
       this.views=dato;
    }


    public static ViewAccess valueOf(CategoriaEmpleado categoria){
       switch (categoria){
           case admin:return Admin;
           case agenteInmobilario:return Agente;
           default:return Default;
       }
    }

    private static ArrayList<DashboardViewType> getDefaultViews(){
        ArrayList<DashboardViewType> views= new ArrayList<DashboardViewType>();
        views.add(DashboardViewType.INICIO);
        return views;

    }
    private static ArrayList<DashboardViewType> getRecoveryViews(){
        ArrayList<DashboardViewType> views= new ArrayList<DashboardViewType>();
        views.add(DashboardViewType.INICIO);
        views.add(DashboardViewType.EMPLEADOS);
        return views;

    }
    private static ArrayList<DashboardViewType> getAgenteViews(){
        ArrayList<DashboardViewType> views= new ArrayList<DashboardViewType>();
        views.add(DashboardViewType.INICIO);
        views.add(DashboardViewType.PERSONAS);
        views.add(DashboardViewType.INMUEBLES);
        views.add(DashboardViewType.PUBLICACIONES);
        views.add(DashboardViewType.CONTRATOS);
        views.add(DashboardViewType.COBROS);
        views.add(DashboardViewType.REPORTES);
        views.add(DashboardViewType.MOVIMIENTOS);
        return views;

    }

    private static ArrayList<DashboardViewType> getAdminViews(){
       ArrayList<DashboardViewType> views= new ArrayList<DashboardViewType>();
        views.addAll(Arrays.asList(DashboardViewType.values()));
       return views;

    }

}
