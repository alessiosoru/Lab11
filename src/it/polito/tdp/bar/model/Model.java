package it.polito.tdp.bar.model;

public class Model {

	Simulatore sim = new Simulatore();
	
	public int getClientiTotali() {
		return sim.getNumeroClientiTotali();
	}

	public int getClientiSoddisfatti() {
		return sim.getNumeroClientiSoddfisfatti();
	}

	public int getClientiInsoddisfatti() {
		return sim.getNumeroClientiInsoddfisfatti();
	}
	
	public void simula() {
		sim.init();
		sim.run();
	}

}
