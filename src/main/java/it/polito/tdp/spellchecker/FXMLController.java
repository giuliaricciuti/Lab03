package it.polito.tdp.spellchecker;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import it.polito.tdp.spellchecker.model.Dictionary;
import it.polito.tdp.spellchecker.model.RichWord;
import it.polito.tdp.spellchecker.model.Dictionary.Languages;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class FXMLController {

	private Dictionary dizionario;
	private List<String> inputTextList;
	
	// Flag to select the array type used for the search
	//using either Dictionary.ArrayType.Simple or 
	// Dictionary.ArrayType.Linear or Dictionary.ArrayType.Dichotomic
	private final static Dictionary.SearchType searchType = Dictionary.SearchType.Simple;
	
	// Flag to select the array type used for the search
	//using either Dictionary.ArrayType.ArrayList or 
	// Dictionary.ArrayType.LinkedList
	private final static Dictionary.ArrayType arrayType = Dictionary.ArrayType.ArrayList;
	
	
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnSpellCheck;

    @FXML
    private ComboBox<Dictionary.Languages> cmbLanguage;

    @FXML
    private Label lblErrors;

    @FXML
    private Label lblTime;

    @FXML
    private TextArea txtInput;

    @FXML
    private TextArea txtResult;

    
    @FXML
    /**
     * Funzione chiamata quando viene selezionata una scelta dal menu a tendina
     * Quando viene selezionata la lingua, viene anche caricato il dizionario corrispondente
     * @param event
     */
    void selectLanguage(ActionEvent event) {
    	Dictionary.Languages linguaSelezionata = cmbLanguage.getValue();
    	try{
    		this.dizionario.loadDictionary(linguaSelezionata, arrayType);
    		txtInput.setDisable(false);
    		this.btnClear.setDisable(false);
    		this.btnSpellCheck.setDisable(false);
    		this.txtResult.clear();
    	}catch(IllegalArgumentException e) {
    		this.txtResult.setText("Selezionare una lingua per continuare!");
    		txtInput.setDisable(true);
    		this.btnClear.setDisable(true);
    		this.btnSpellCheck.setDisable(true);
    		return;
    	}catch(IOException e) {
    		this.txtResult.setText("Errore nella lettura del dizionario");
    		txtInput.setDisable(true);
    		this.btnClear.setDisable(true);
    		this.btnSpellCheck.setDisable(true);
    		return;
    	}
    }
    
    
    @FXML
    /**
     * Funzione che viene invocata quando viene cliccato il pulsante Spell Check
     * @param event
     */
    void doCheck(ActionEvent event) {
    	//Per prima cosa, viene letto il testo da correggere e si verifica che non sia una 
    	// stringa vuota
    	String inputText = this.txtInput.getText();
    	if (inputText.isEmpty()) {
			this.txtResult.setText("Inserire un testo da correggere!");
			return;
		}

    	
    	//La stringa letta viene quindi divisa in tokens. In questa implementazione
    	//si utilizza uno StringTokenizer per farlo, ma si sarebbe potuto anche farlo con una replaceAll
    	// e poi facendo split, ovvero:
       	//		inputText = inputText.replaceAll("[.,\\/#!$%\\^&\\*;:{}=\\-_`~()\\[\\]]\n", "");
    	//		String[] tokens = inputText.split(" ");
    	
    	List<String> inputTextList = new LinkedList<String>();
		StringTokenizer st = new StringTokenizer(inputText, "\n[.,?\\/#!$%^&*;:{}=-_`~()] ");
		while (st.hasMoreTokens()) {
			inputTextList.add(st.nextToken());
		}
    	
				
		//Quindi, viene effettuato lo spellcheck, token per token
		long start = System.nanoTime();
		List<RichWord> outputTextList;
		
		switch(this.searchType) {
			case Simple:
				outputTextList = dizionario.spellCheckText(inputTextList, arrayType);
				break;
			case Linear:
				outputTextList = dizionario.spellCheckTextLinear(inputTextList, arrayType);
				break;
			case Dichotomic:
				outputTextList = dizionario.spellCheckTextDichotomic(inputTextList, arrayType);
				break;
			default:
				outputTextList = dizionario.spellCheckText(inputTextList, arrayType);
				break;
		}
		long end = System.nanoTime();
		
		
		//Infine, vengono contati gli errori e vengono poi stampate le statistiche
		int numErrori = 0;
		StringBuilder richText = new StringBuilder();

		for (RichWord r : outputTextList) {
			if (!r.isCorrect()) {
				numErrori++;
				richText.append(r.getWord() + "\n");
			}
		}
		this.txtResult.setText(richText.toString());
		this.lblTime.setText("Spell check completed in " + (end - start) / 1E9 + " seconds");
		this.lblErrors.setText("The text contains " + numErrori + " errors");
    }
    
    
    

    @FXML
    /**
     * Funzione che resetta l'interfaccia grafica quando viene cliccato il pulsante Clear
     * @param event
     */
    void doClear(ActionEvent event) {
    	this.txtInput.clear();
    	this.txtResult.clear();
		this.lblErrors.setText("Number of Errors:");
		this.lblTime.setText("Spell Check Status:");
    }
    
    
    /**
     * Funzione utilizzata nell'EntryPoint per settare il modello usato dal FXMLController
     * @param model
     */
    public void setModel(Dictionary model) {
 		this.dizionario = model;
    }

    
    
    @FXML
    void initialize() {
        assert btnClear != null : "fx:id=\"btnClear\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnSpellCheck != null : "fx:id=\"btnSpellCheck\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbLanguage != null : "fx:id=\"cmbLanguage\" was not injected: check your FXML file 'Scene.fxml'.";
        assert lblErrors != null : "fx:id=\"lblErrors\" was not injected: check your FXML file 'Scene.fxml'.";
        assert lblTime != null : "fx:id=\"lblTime\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtInput != null : "fx:id=\"txtInput\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
        
        //Inizializzazione degli items visualizzati nel menu a tendina
        cmbLanguage.getItems().add(Languages.Italian);
        cmbLanguage.getItems().add(Languages.English);
        
        //Inizializzazione di alcuni elementi grafici
        this.txtInput.clear();
    	this.txtResult.clear();
		this.lblErrors.setText("Number of Errors:");
		this.lblTime.setText("Spell Check Status:");
		this.txtResult.setText("Selezionare una lingua per continuare!");
		txtInput.setDisable(true);
		txtInput.setEditable(true);
		this.btnClear.setDisable(true);
		this.btnSpellCheck.setDisable(true);

    }

}
