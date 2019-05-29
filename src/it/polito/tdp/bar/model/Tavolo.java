package it.polito.tdp.bar.model;

import java.time.LocalTime;

public class Tavolo implements Comparable<Tavolo> {

	private int numMaxPersone;
	private boolean occupato;
	private LocalTime orarioLiberazione;
	public Tavolo(int numMaxPersone, boolean occupato, LocalTime orarioLiberazione) {
		super();
		this.numMaxPersone = numMaxPersone;
		this.occupato = occupato;
		this.orarioLiberazione = orarioLiberazione;
	}
	public int getNumMaxPersone() {
		return numMaxPersone;
	}
	public void setNumMaxPersone(int numMaxPersone) {
		this.numMaxPersone = numMaxPersone;
	}
	public boolean isOccupato() {
		return occupato;
	}
	public void setOccupato(boolean occupato) {
		this.occupato = occupato;
	}
	public LocalTime getOrarioLiberazione() {
		return orarioLiberazione;
	}
	public void setOrarioLiberazione(LocalTime orarioLiberazione) {
		this.orarioLiberazione = orarioLiberazione;
	}
	@Override
	public int compareTo(Tavolo other) { // priorità al tavolo più piccolo
		return this.numMaxPersone-other.numMaxPersone;
	}
	
	
}
