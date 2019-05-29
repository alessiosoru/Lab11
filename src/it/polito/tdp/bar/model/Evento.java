package it.polito.tdp.bar.model;

import java.time.Duration;
import java.time.LocalTime;

public class Evento implements Comparable<Evento>{
	
	public enum TipoEvento{
		ARRIVO_GRUPPO_CLIENTI,
		USCITA_GRUPPO_CLIENTI
	}
	
	private LocalTime time;
	private TipoEvento tipo;
	private int numPersone;
	private Duration durata;
	private float tolleranza;
	
	

	public Evento(LocalTime time, TipoEvento tipo, int numPersone, Duration durata, float tolleranza) {
		super();
		this.time = time;
		this.tipo = tipo;
		this.numPersone = numPersone;
		this.durata = durata;
		this.tolleranza = tolleranza;
	}

	public LocalTime getTime() {
		return time;
	}



	public void setTime(LocalTime time) {
		this.time = time;
	}



	public TipoEvento getTipo() {
		return tipo;
	}



	public void setTipo(TipoEvento tipo) {
		this.tipo = tipo;
	}



	public int getNumPersone() {
		return numPersone;
	}



	public void setNumPersone(int numPersone) {
		this.numPersone = numPersone;
	}



	public Duration getDurata() {
		return durata;
	}



	public void setDurata(Duration durata) {
		this.durata = durata;
	}



	public float getTolleranza() {
		return tolleranza;
	}



	public void setTolleranza(float tolleranza) {
		this.tolleranza = tolleranza;
	}

	@Override
	public String toString() {
		return "Evento [time=" + time + ", tipo=" + tipo + ", numPersone=" + numPersone + ", durata=" + durata
				+ ", tolleranza=" + tolleranza + "]";
	}

	@Override
	public int compareTo(Evento other) {
		return this.time.compareTo(other.time);
	}

}
