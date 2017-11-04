package com.TpFinal.services;

import com.TpFinal.data.dao.DAOCitaImpl;
import com.TpFinal.dto.cita.Cita;
import com.TpFinal.dto.contrato.ContratoDuracion;
import com.TpFinal.dto.persona.CategoriaEmpleado;
import com.TpFinal.dto.persona.Empleado;
import org.h2.table.Plan;

import java.util.ArrayList;
import java.util.List;

public class CitaService {
    DAOCitaImpl dao;

    public CitaService(){
        dao =new DAOCitaImpl();

    }

    public boolean saveOrUpdate(Cita cita) {
        return dao.saveOrUpdate(cita);
    }



    public boolean addCita(Cita cita){
       boolean b= saveOrUpdate(cita);
        Long id=null;
        if(b){
            List<Cita> citas=readAll();
            for (Cita citaGuardada:citas) {
                id=citaGuardada.getId();
                citaGuardada.setId(null);
                if (citaGuardada.equals(cita)) {
                    cita.setId(id);
                    break;
                }
            }
        }


        Planificador.get().removeCita(cita);
        Planificador.get().addCita(cita);


       return b;
    }

    public Cita getCitaFromTriggerKey(String triggerKey){
        int corte=triggerKey.indexOf('-');

        for (Cita cita:readAll()){
            if(cita.getId().toString().equals(triggerKey.substring(0,corte))){
                return cita;
            }
        }
        return null;


    }

    public void agregarTriggers(Cita cita){
        Planificador.get().removeCita(cita);
        Planificador.get().addCita(cita);
    }

    public Cita getUltimaAgregada() {
        List<Cita>citas=dao.readAllActives();
        citas.sort((c1,c2)-> c2.getId().compareTo(c1.getId()));
        return citas.get(0);
    }

    public boolean editCita(Cita cita){
        System.out.println("EDITADA "+cita);
       agregarTriggers(cita);
       return saveOrUpdate(cita);

    }


    public boolean delete(Cita p) {

       return dao.logicalDelete(p);

    }

    public boolean deleteCita(Cita p) {
        System.out.println("BORRADA"+p);
        boolean ret1=true;
        boolean ret2=true;

        ret1=dao.logicalDelete(p);
        if(!ret1){
            System.err.println("Error al Borrar la cita..");
        }
        ret2=Planificador.get().removeCita(p);
        if(!ret2){
            System.err.println("Error al Borrar los recodatorios de la cita... " +
                    "\nes probable que ya se hayan detonado los triggers");
        }


        return ret1;
    }

    public List<Cita> readAllFromUser(Empleado user){
        if(user.getCategoriaEmpleado().equals(CategoriaEmpleado.admin)){
            return readAll();
        }
        List<Cita> ret=new ArrayList<>();
        for (Cita cita:readAll()){
            if(cita.getEmpleado().equals(user)){
               ret.add(cita);
            }}

        return ret;
    }

    public List<Cita> readAll(){
        return dao.readAllActives();
    }






}
