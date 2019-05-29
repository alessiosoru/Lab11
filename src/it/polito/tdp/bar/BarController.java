package it.polito.tdp.bar;

import java.net.URL;
import java.util.ResourceBundle;

import it.polito.tdp.bar.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class BarController {

	Model model;
	
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button buttonSimula;

    @FXML
    private TextArea txtResult;

    @FXML
    void handleSimula(ActionEvent event) {
    	
    	this.txtResult.clear();
    	this.model.simula();
    	this.txtResult.appendText("Clienti totali: "+model.getClientiTotali()+
    			"\nClienti soddisfatti: "+model.getClientiSoddisfatti()+
    			"\nClienti insoddisfatti: "+model.getClientiInsoddisfatti());
    }

    @FXML
    void initialize() {
        assert buttonSimula != null : "fx:id=\"buttonSimula\" was not injected: check your FXML file 'Bar.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Bar.fxml'.";

    }

	public void setModel(Model model) {
		this.model = model;
	}
}