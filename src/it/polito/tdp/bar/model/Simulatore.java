package it.polito.tdp.bar.model;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import it.polito.tdp.bar.model.Evento.TipoEvento;

public class Simulatore {
	
	// Coda degli evventi
	private PriorityQueue<Evento> queue = new PriorityQueue<>();
	
	// Modello del mondo
	private PriorityQueue<Tavolo> tavoli;
	
	// Parametri di Simulazione
	
	private LocalTime oraInizio = LocalTime.of(8, 0);
//	private LocalTime oraFine = LocalTime.of(20, 0);
	
	private int NUM_EVENTI = 2000;
	
	private int MAX_TAVOLI_10 =2; // 2 TAVOLI DA 10 POSTI E COSì VIA
	private int MAX_TAVOLI_8 =4;
	private int MAX_TAVOLI_6 =4;
	private int MAX_TAVOLI_4 =5;
	
	
	private int MIN_DURATA_INTERVALLO_EVENTI = 1;
	private int MAX_DURATA_INTERVALLO_EVENTI = 10;
	
	private int MIN_PERSONE_GRUPPO = 1;
	private int MAX_PERSONE_GRUPPO = 10;
	
	private int MIN_PERMANENZA = 60;
	private int MAX_PERMANENZA = 120;
	
	private float MIN_TOLLERANZA = (float) 0.0;
	private float MAX_TOLLERANZA = (float) 0.9;
	
	// Statistiche da calcolare
	private int numeroClientiTotali;
	private int numeroClientiSoddfisfatti;
	private int numeroClientiInsoddfisfatti;	
	
	// variabili interne

	private Map<LocalTime, Tavolo> tavoliOccupati; // orarioFine/tavolo
	private List<Duration> durateIntervalloEventi;
	private List<Integer> numeriPersoneGruppo;
	private List<Duration> duratePermanenza;
	private List<Float> tolleranze;
	private Random rand = new Random();
	
	public Simulatore(){
		this.tavoli= new PriorityQueue();
		this.tavoliOccupati = new HashMap<>();
		this.durateIntervalloEventi = new ArrayList<>();
		this.numeriPersoneGruppo = new ArrayList<>();
		this.duratePermanenza = new ArrayList<>();
		this.tolleranze = new ArrayList<>();
		
		// inserisco tutti gli intervalli per i vari parametri
		for(int i=this.MIN_DURATA_INTERVALLO_EVENTI; i<=this.MAX_DURATA_INTERVALLO_EVENTI;i++)
			this.durateIntervalloEventi.add(Duration.ofMinutes(i));
		for(int i = this.MIN_PERSONE_GRUPPO; i<=this.MAX_PERSONE_GRUPPO;i++)
			this.numeriPersoneGruppo.add(i);
		for(int i=this.MIN_PERMANENZA;i<=this.MAX_PERMANENZA;i++)
			this.duratePermanenza.add(Duration.ofMinutes(i));
		for(float i =this.MIN_TOLLERANZA;i<=this.MAX_TOLLERANZA;i=(float) (i+0.1))
			this.tolleranze.add(i);
	}

	public void init() {
		// creo variabili e inizializzo
		for(int i = 0; i< this.MAX_TAVOLI_10; i++)
			this.tavoli.add(new Tavolo(this.MAX_TAVOLI_10, false, null));
		for(int i = 0; i< this.MAX_TAVOLI_8; i++)
			this.tavoli.add(new Tavolo(this.MAX_TAVOLI_8, false, null));
		for(int i = 0; i< this.MAX_TAVOLI_6; i++)
			this.tavoli.add(new Tavolo(this.MAX_TAVOLI_6, false, null));
		for(int i = 0; i< this.MAX_TAVOLI_4; i++)
			this.tavoli.add(new Tavolo(this.MAX_TAVOLI_4, false, null));
//		
//		this.tavoliLiberi.put(10,2);
//		this.tavoliLiberi.put(8, 4);
//		this.tavoliLiberi.put(6, 4);
//		this.tavoliLiberi.put(4, 5);
//		
		
		
		// creo eventi iniziali
		queue.clear();		
		LocalTime oraArrivo = this.oraInizio;
		int numeroPersone;
		Duration permanenza;
		float tolleranza;
		for(int i = 0; i<this.NUM_EVENTI;i++) {
			numeroPersone = this.randomNumPersone();
			permanenza = this.randomPermanenza();
			tolleranza = this.randomTolleranza();
			queue.add(new Evento(oraArrivo, TipoEvento.ARRIVO_GRUPPO_CLIENTI,
					numeroPersone, permanenza, tolleranza));
			oraArrivo = oraArrivo.plus(this.randomTempoArrivo());
		}
		
		// eventuale polling
		
		// resetto le statistiche
		this.numeroClientiInsoddfisfatti=0;
		this.numeroClientiSoddfisfatti=0;
		this.numeroClientiTotali=0;
		
	}
	
	public void run() {
		int evento = 0;
		while(!queue.isEmpty()) {
			Evento ev = queue.poll();
			
			switch(ev.getTipo()) {
			
			case ARRIVO_GRUPPO_CLIENTI:
				this.numeroClientiTotali = this.numeroClientiTotali+ev.getNumPersone();
				// se tavolo libero, e se soddisfa requisiti assegno tavolo
				// eventuale orario liberazione tavolo
				LocalTime orarioLiberazione = ev.getTime().plus(ev.getDurata());
				if(assegnoTavolo(ev.getNumPersone(), orarioLiberazione)) {					
					// creo evento uscita dopo tot tempo
					queue.add(new Evento(orarioLiberazione, 
							TipoEvento.USCITA_GRUPPO_CLIENTI, ev.getNumPersone(), 
							Duration.ofMinutes(0), 0));
					// aggiorno numero clienti soddisfazione
					this.numeroClientiSoddfisfatti+=ev.getNumPersone();
				} else { // altrimenti mando al bancone oppure via					
					// verifico bancone
					if(accettaBancone(ev.getTolleranza())) {
						this.numeroClientiSoddfisfatti+=ev.getNumPersone();
					} else {
						this.numeroClientiInsoddfisfatti+=ev.getNumPersone();						
					}
				}
				break;
			case USCITA_GRUPPO_CLIENTI:
				// libero il tavolo --> occupato false
				if(this.tavoliOccupati.get(ev.getTime())!=null) {
						Tavolo t = this.tavoliOccupati.get(ev.getTime());
						t.setOccupato(false);
						t.setOrarioLiberazione(null);
						this.tavoliOccupati.remove(ev.getTime());
				}
				break;
			}
		}
		
	}

	private boolean assegnoTavolo(int numPersone, LocalTime orarioLiberazione) {
		for(Tavolo t : tavoli) { // cerco il primo tavolo libero e occupabile, coda con priorità di numero persone
			if(!t.isOccupato() && (t.getNumMaxPersone()/2<=numPersone)) {
				t.setOccupato(true);
				t.setOrarioLiberazione(orarioLiberazione);
				this.tavoliOccupati.put(orarioLiberazione, t);
				return true;
			}
		}
		return false;
	}
	
	private boolean accettaBancone(float tolleranza) {
		// genero un intero random da uno a 9 e verifico se sta all'interno del range
		// di tolleranza, ovvero se è innfferiore al valore tolleranza
		float tollera = rand.nextFloat();
		if(tollera<=tolleranza && tolleranza!=0.0)
			return true;
		else
			return false;
	}
	
	public Duration randomTempoArrivo() { // FACCIO DIRETTAMENTE CON UN RANDOM DI INTERI
		// randomizzo il tempo dia arrivo da 1 a 10 minuti
		int i = rand.nextInt(this.MAX_DURATA_INTERVALLO_EVENTI);
		Duration intervalloArrivo = this.durateIntervalloEventi.get(i);
		return intervalloArrivo;
	}
	
	public int randomNumPersone() {
		// randomizzo il numero di persone che arrivano tra 1 e 10
		int i = rand.nextInt(this.MAX_PERSONE_GRUPPO);
		return this.numeriPersoneGruppo.get(i);
	}
	
	public Duration randomPermanenza() {
		int i = rand.nextInt(this.MAX_PERMANENZA-this.MIN_PERMANENZA);
		Duration permanenza = this.duratePermanenza.get(i);
		return permanenza;
	}
	
	private float randomTolleranza() {
		int tollMax =  (int) (this.MAX_TOLLERANZA*10);
		int i = rand.nextInt(tollMax);
		float tolleranza = this.tolleranze.get(i);
		return tolleranza;
	}

	public PriorityQueue<Evento> getQueue() {
		return queue;
	}

	public void setQueue(PriorityQueue<Evento> queue) {
		this.queue = queue;
	}

	public PriorityQueue<Tavolo> getTavoli() {
		return tavoli;
	}

	public void setTavoli(PriorityQueue<Tavolo> tavoli) {
		this.tavoli = tavoli;
	}

	public LocalTime getOraInizio() {
		return oraInizio;
	}

	public void setOraInizio(LocalTime oraInizio) {
		this.oraInizio = oraInizio;
	}

	public int getNUM_EVENTI() {
		return NUM_EVENTI;
	}

	public void setNUM_EVENTI(int nUM_EVENTI) {
		NUM_EVENTI = nUM_EVENTI;
	}

	public int getMAX_TAVOLI_10() {
		return MAX_TAVOLI_10;
	}

	public void setMAX_TAVOLI_10(int mAX_TAVOLI_10) {
		MAX_TAVOLI_10 = mAX_TAVOLI_10;
	}

	public int getMAX_TAVOLI_8() {
		return MAX_TAVOLI_8;
	}

	public void setMAX_TAVOLI_8(int mAX_TAVOLI_8) {
		MAX_TAVOLI_8 = mAX_TAVOLI_8;
	}

	public int getMAX_TAVOLI_6() {
		return MAX_TAVOLI_6;
	}

	public void setMAX_TAVOLI_6(int mAX_TAVOLI_6) {
		MAX_TAVOLI_6 = mAX_TAVOLI_6;
	}

	public int getMAX_TAVOLI_4() {
		return MAX_TAVOLI_4;
	}

	public void setMAX_TAVOLI_4(int mAX_TAVOLI_4) {
		MAX_TAVOLI_4 = mAX_TAVOLI_4;
	}

	public int getMIN_DURATA_INTERVALLO_EVENTI() {
		return MIN_DURATA_INTERVALLO_EVENTI;
	}

	public void setMIN_DURATA_INTERVALLO_EVENTI(int mIN_DURATA_INTERVALLO_EVENTI) {
		MIN_DURATA_INTERVALLO_EVENTI = mIN_DURATA_INTERVALLO_EVENTI;
	}

	public int getMAX_DURATA_INTERVALLO_EVENTI() {
		return MAX_DURATA_INTERVALLO_EVENTI;
	}

	public void setMAX_DURATA_INTERVALLO_EVENTI(int mAX_DURATA_INTERVALLO_EVENTI) {
		MAX_DURATA_INTERVALLO_EVENTI = mAX_DURATA_INTERVALLO_EVENTI;
	}

	public int getMIN_PERSONE_GRUPPO() {
		return MIN_PERSONE_GRUPPO;
	}

	public void setMIN_PERSONE_GRUPPO(int mIN_PERSONE_GRUPPO) {
		MIN_PERSONE_GRUPPO = mIN_PERSONE_GRUPPO;
	}

	public int getMAX_PERSONE_GRUPPO() {
		return MAX_PERSONE_GRUPPO;
	}

	public void setMAX_PERSONE_GRUPPO(int mAX_PERSONE_GRUPPO) {
		MAX_PERSONE_GRUPPO = mAX_PERSONE_GRUPPO;
	}

	public int getMIN_PERMANENZA() {
		return MIN_PERMANENZA;
	}

	public void setMIN_PERMANENZA(int mIN_PERMANENZA) {
		MIN_PERMANENZA = mIN_PERMANENZA;
	}

	public int getMAX_PERMANENZA() {
		return MAX_PERMANENZA;
	}

	public void setMAX_PERMANENZA(int mAX_PERMANENZA) {
		MAX_PERMANENZA = mAX_PERMANENZA;
	}

	public float getMIN_TOLLERANZA() {
		return MIN_TOLLERANZA;
	}

	public void setMIN_TOLLERANZA(float mIN_TOLLERANZA) {
		MIN_TOLLERANZA = mIN_TOLLERANZA;
	}

	public float getMAX_TOLLERANZA() {
		return MAX_TOLLERANZA;
	}

	public void setMAX_TOLLERANZA(float mAX_TOLLERANZA) {
		MAX_TOLLERANZA = mAX_TOLLERANZA;
	}

	public int getNumeroClientiTotali() {
		return numeroClientiTotali;
	}

	public void setNumeroClientiTotali(int numeroClientiTotali) {
		this.numeroClientiTotali = numeroClientiTotali;
	}

	public int getNumeroClientiSoddfisfatti() {
		return numeroClientiSoddfisfatti;
	}

	public void setNumeroClientiSoddfisfatti(int numeroClientiSoddfisfatti) {
		this.numeroClientiSoddfisfatti = numeroClientiSoddfisfatti;
	}

	public int getNumeroClientiInsoddfisfatti() {
		return numeroClientiInsoddfisfatti;
	}

	public void setNumeroClientiInsoddfisfatti(int numeroClientiInsoddfisfatti) {
		this.numeroClientiInsoddfisfatti = numeroClientiInsoddfisfatti;
	}

	public Map<LocalTime, Tavolo> getTavoliOccupati() {
		return tavoliOccupati;
	}

	public void setTavoliOccupati(Map<LocalTime, Tavolo> tavoliOccupati) {
		this.tavoliOccupati = tavoliOccupati;
	}

	public List<Duration> getDurateIntervalloEventi() {
		return durateIntervalloEventi;
	}

	public void setDurateIntervalloEventi(List<Duration> durateIntervalloEventi) {
		this.durateIntervalloEventi = durateIntervalloEventi;
	}

	public List<Integer> getNumeriPersoneGruppo() {
		return numeriPersoneGruppo;
	}

	public void setNumeriPersoneGruppo(List<Integer> numeriPersoneGruppo) {
		this.numeriPersoneGruppo = numeriPersoneGruppo;
	}

	public List<Duration> getDuratePermanenza() {
		return duratePermanenza;
	}

	public void setDuratePermanenza(List<Duration> duratePermanenza) {
		this.duratePermanenza = duratePermanenza;
	}

	public List<Float> getTolleranze() {
		return tolleranze;
	}

	public void setTolleranze(List<Float> tolleranze) {
		this.tolleranze = tolleranze;
	}

	public Random getRand() {
		return rand;
	}

	public void setRand(Random rand) {
		this.rand = rand;
	}
	
	
}
