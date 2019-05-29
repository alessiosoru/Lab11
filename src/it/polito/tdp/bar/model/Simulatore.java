package it.polito.tdp.bar.model;

import java.time.Duration;
import java.util.PriorityQueue;

public class Simulatore {
	
	// Coda degli evventi
//	private PriorityQueue<Evento> queue = new PriorityQueue<>();
	
	// Modello del mondo
	
	// Parametri di Simulazione
//	private Duration T_ARRIVAL = randomArrivo();
	
	// Statistiche da calcolare
	
	// variabili interne
	private Duration intervalloAttuale = Duration.ofMinutes(1);
	
	public Simulatore(){
		
	}

	public void init() {
		// creo variabili e inizializzo
		
		// creo eventi iniziali
//		queue.clear();
		
		// eventuale polling
		
		// resetto le statistiche
		
	}
	
	public void run() {
		
	}
	
//	private void randomArrivo() {
//		if(this.intervalloAttuale.compareTo(Duration.ofMinutes(10))<0)
//			return this.intervalloAttuale.plus(Duration.ofMinutes(1));
//		else
//			re
//		return null;
//	}
}
