package com.TpFinal.dto.cita;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.TpFinal.dto.BorradoLogico;
import com.TpFinal.dto.EstadoRegistro;
import com.TpFinal.dto.Identificable;
import com.TpFinal.dto.interfaces.Messageable;
import com.TpFinal.dto.persona.Empleado;

@Entity
@Table(name = "citas")
public class Cita implements Identificable, BorradoLogico, Messageable {

	//Mod by agus(calendario)
	public enum State {
		empty,
		planned,
		confirmed
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_cita")
	private Long id;
	@Enumerated(EnumType.STRING)
	@Column(name = "estado_registro")
	@NotNull
	private EstadoRegistro estadoRegistro = EstadoRegistro.ACTIVO;
	//Mod by agus(calendario)
	@Column(name = "fecha_hora")
	private LocalDateTime fechaInicio;
	@Column(name = "fecha_fin")
	private LocalDateTime fechaFin;
	@Column(name = "longTime")
	private boolean longTime;
	@Enumerated(EnumType.STRING)
	@Column(name = "state")
	State state;
	//Mod by agus (calendario)
	@Column(name = "direccion_lugar")
	private String direccionLugar;
	@Column(name = "citado")
	private String citado;
	@Column(name = "observaciones")
	private String observaciones;
	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_cita")
	TipoCita tipoDeCita;
	@OneToMany(mappedBy = "cita", fetch = FetchType.EAGER)
	@Cascade({ CascadeType.ALL })
	protected Set<Recordatorio> recordatorios = new HashSet<>();
	@ManyToOne(fetch = FetchType.EAGER)
	@Cascade({ CascadeType.SAVE_UPDATE, CascadeType.MERGE })
	@JoinColumn(name = "id_empleado")
	private Empleado empleado;

	public Cita() {
		this.empleado=new Empleado();
	}

	private Cita(Builder b) {
		this.fechaInicio = b.fechahora;
		this.citado = b.citado;
		this.direccionLugar = b.direccionLugar;
		this.observaciones = b.observaciones;
		this.tipoDeCita = b.tipoDeCita;
		this.empleado=b.empleado;
	}

	public LocalDateTime getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(LocalDateTime fechaFin) {
		this.fechaFin = fechaFin;
	}

	public String getName() {
		return getDetails();
	}
	public String getDetails() {
		LocalDateTime i=
				this.getFechaInicio();

		return "Cita con "
		+citado+"<br>"+this.tipoDeCita+"<br> el "
		+i.getDayOfMonth()
		+"/"+i.getMonthValue()+"/"+i.getYear()+
		" a las "+i.getHour()+":"+i.getMinute();

	}

	public boolean isLongTimeEvent() {
		return longTime;
	}

	public void setLongTimeEvent(boolean b) {
		longTime=b;
	}

	public boolean isEditable() {
		return true;
	}

	public State getState() {
		return State.planned;
	}

	public void setState(State state){
		this.state=state;
	}

	public Set<Recordatorio> getRecordatorios() {
		return recordatorios;
	}

	public void addRecordatorio(Recordatorio recordatorio) {
		if (!this.recordatorios.contains(recordatorio)) {
			this.recordatorios.add(recordatorio);
			recordatorio.setCita(this);
		}
	}

	public void removeRecordatorio(Recordatorio recordatorio) {
		if (this.recordatorios.contains(recordatorio)) {
			this.recordatorios.remove(recordatorio);
			recordatorio.setCita(null);
		}
	}

	public Empleado getEmpleado() {
		return empleado;
	}

	public void setEmpleado(Empleado emp) {

		if (this.empleado != null && !this.empleado.equals(emp)) {
			this.empleado.removeCita(this);
		} 
		this.empleado=emp;
		if(emp!=null && !emp.getCalendarioPersonal().contains(this)) {
			emp.addCita(this);
		}
	}

	public LocalDateTime getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDateTime fechaHora) {
		this.fechaInicio = fechaHora;
	}

	public String getDireccionLugar() {
		return direccionLugar;
	}

	public void setDireccionLugar(String direccionLugar) {
		this.direccionLugar = direccionLugar;
	}

	public String getCitado() {
		return citado;
	}

	public void setCitado(String citado) {
		this.citado = citado;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public TipoCita getTipoDeCita() {
		return tipoDeCita;
	}

	public void setTipoDeCita(TipoCita tipoDeCita) {
		this.tipoDeCita = tipoDeCita;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setEstadoRegistro(EstadoRegistro estado) {
		this.estadoRegistro = estado;
	}

	@Override
	public EstadoRegistro getEstadoRegistro() {
		return this.estadoRegistro;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return 31;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Cita))
			return false;
		Cita other = (Cita) obj;
		return getId() != null && Objects.equals(getId(), other.getId());
	}

	@Override
	public String toString() {
		return "Cita [\nid=" + id + "\nestadoRegistro=" + estadoRegistro + "\nfechaHora=" + fechaInicio
				+ "\ndireccionLugar=" + direccionLugar + "\ncitado=" + citado + "\nobservaciones=" + observaciones
				+ "\ntipoDeCita=" + tipoDeCita + "\nrecordatorios=" + recordatorios + "\n]";
	}

	@Override
	public String getMessage() {
		return "Hora: "+dejarLindo(this.fechaInicio.getHour())+":"+dejarLindo(this.fechaInicio.getMinute())
		+"\n"+"Direccion: "+this.direccionLugar+"\n"+"Tipo de cita: "+this.tipoDeCita;
	}

	@Override
	public String getTitulo() {
		return "Cita con "+this.citado;
	}

	private String dejarLindo(int horaMinuto) {
		String ret=String.valueOf(horaMinuto);
		if(horaMinuto<10)
			ret="0"+String.valueOf(horaMinuto);
		return ret;
	}



	public static class Builder {

		private TipoCita tipoDeCita;
		private String observaciones;
		private String direccionLugar;
		private String citado;
		private LocalDateTime fechahora;
		private Empleado empleado;

		public Builder setTipoDeCita(TipoCita tipoDeCita) {
			this.tipoDeCita = tipoDeCita;
			return this;
		}

		public Builder setObservaciones(String observaciones) {
			this.observaciones = observaciones;
			return this;
		}

		public Builder setDireccionLugar(String direccionLugar) {
			this.direccionLugar = direccionLugar;
			return this;
		}

		public Builder setCitado(String citado) {
			this.citado = citado;
			return this;
		}

		public Builder setFechahora(LocalDateTime fechahora) {
			this.fechahora = fechahora;
			return this;
		}
		
		public Builder setEmpleado(Empleado empleado) {
			this.empleado=empleado;
			return this;
		}

		public Cita build() {
			return new Cita(this);
		}

	}


}
