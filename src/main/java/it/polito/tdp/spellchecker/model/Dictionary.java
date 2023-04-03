/**
 * 
 */
package it.polito.tdp.spellchecker.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class Dictionary {
	
	//Enum che usiamo per rappresentare le lingue supportate
	// In alternativa possiamo anche usare una semplice stringa
	public enum Languages{
		Italian,
		English
	}
	
	//Enum che utilizziamo per rappresentare i tipi di array 
	//per implementare diverse cersioni dello spell check
	public enum ArrayType{
		LinkedList,
		ArrayList
	}
	
	//Enum che utilizziamo per rappresentare i tipi di ricerca
	//per fare lo spellcheck
	public enum SearchType{
		Simple,
		Linear,
		Dichotomic
	}
	
	
	
	private List<String> dizionario;  	// Lista che contiene tutte le parole del dizionario attualmente selezionato
	private Languages language;			// La lingua che specifica il dizionario attualmente selezionato
	
	
	// Costruttore vuoto
	public Dictionary() {}
	
	
	
	/**
	 * Funzione che carica un dizionario, inserendo tutte le parole nella collezione privata `dizionario`
	 * @param language la lingua da caricare
	 * @return un booleano che è true se il vocabolario è stato caricato con successo, e false se ci sono stati errori.
	 * @throws IllegalArgumentException se viene fornito come argomento una lingua non valida (null)
	 * @throws IOException se ci sono stati errori nella lettura del dizionario
	 */
	public void loadDictionary(Languages language, ArrayType type) throws IOException, IllegalArgumentException{
		//Per prima cosa verifichiamo che la lingua selezionata non sia null,
		// altrimenti lanciamo una eccezione
		if (language == null) {
			throw new IllegalArgumentException("Lingua selezionata non valida.");
		}
		
		// Dopodiché, verifichiamo che il dizionario scelto non sia già stato caricato.
		// In tal caso si può evitare di leggerlo nuovamente
		// In realtà, con il menu a tendina questo caso non si verifica mai, ma potrebbe accadere 
		// invocando il metodo manualmente in un file di test.
		if (this.language!=null && this.language.equals(language)){
			System.out.println("Dizionario già letto");
			return;
		}
		
		// Creiamo una struttura dati di appoggio per leggere il dizionario,
		// in modo che se durante la lettura del dizionario non cancelliamo quello vecchio fintanto che
		// la lettura non sia terminata senza errori
		List<String> nuovoDizionario = initializeDictionaryListType(type);
		
		
		// A questo punto, possiamo leggere il file del dizionario e salvare tutte le parole utilizzando
		// un BufferedReader
		try {
			FileReader fr = new FileReader("src/main/resources/" + language.toString() + ".txt");
			BufferedReader br = new BufferedReader(fr);
			String word;

			while ((word = br.readLine()) != null) {
				nuovoDizionario.add(word.toLowerCase());
			}
			br.close();
			
			Collections.sort(nuovoDizionario); // Facciamo il sort della lista, per poi poter implementare i vari metodi di ricerca
			this.language = language;
			this.dizionario = nuovoDizionario;
			System.out.println("Dizionario " + language + " loaded. Found " + nuovoDizionario.size() + " words.");
			
			return;

		} catch (IOException e) {
			System.err.println("Errore nella lettura del dizionario");
			dizionario = null;
			throw(e);
		}

		
	}
	
	
	
	/**
	 * Semplice funzione per controllare lo spelling di una lista di parole
	 * @param inputTextList
	 * @return una lista di RichWord, corrispondente a tutte le parole di input
	 */
	public List<RichWord> spellCheckText(List<String> inputTextList, ArrayType type){
		
		// La lista usata per fare la ricerca é implementata a seconda del tipo scelto,
		// ArrayList o LinkedList, per poterne valutare le differenze
		List<RichWord> parole = initializeListType(type);

		//ora si fa il controllo sulle singole parole, e per ognuna viene 
		// creata una RichWord di risultato
		for (String str : inputTextList) {
			RichWord richWord = new RichWord(str);
			if (dizionario.contains(str.toLowerCase())) {
				richWord.setCorrect(true);
			} else {
				richWord.setCorrect(false);
			}
			parole.add(richWord);
		}

		return parole;
	}
	
	
	
	/**
	 * Funzione per controllare lo spelling di una lista di parole, usando una ricerca lineare
	 * @param inputTextList
	 * @return una lista di RichWord, corrispondente a tutte le parole di input
	 */
	public List<RichWord> spellCheckTextLinear(List<String> inputTextList, ArrayType type) {
		// La lista usata per fare la ricerca é implementata a seconda del tipo scelto,
		// ArrayList o LinkedList, per poterne valutare le differenze
		List<RichWord> parole = initializeListType(type);

		for (String str : inputTextList) {
			RichWord richWord = new RichWord(str);
			
			// per ogni parola da controllare, si fa una ricerca lineare
			boolean found = false;
			for (String word : dizionario) {
				if (word.equalsIgnoreCase(str)) {
					found = true;
					break;
				}
			}
			
			// a seconda dell'esito della ricerca, settiamo la correttezza della RichWord risultato
			if (found) {
				richWord.setCorrect(true);	
			} else {
				richWord.setCorrect(false);
			}
			parole.add(richWord);
		}
		return parole;
	}

	
	
	
	/**
	 * Funzione per controllare lo spelling di una lista di parole, usando una ricerca dicotomica
	 * @param inputTextList
	 * @return una lista di RichWord, corrispondente a tutte le parole di input
	 */
	public List<RichWord> spellCheckTextDichotomic(List<String> inputTextList, ArrayType type) {
		// La lista usata per fare la ricerca é implementata a seconda del tipo scelto,
		// ArrayList o LinkedList, per poterne valutare le differenze
		List<RichWord> parole = initializeListType(type);

		//quindi, eseguiamo la ricerca 
		for (String str : inputTextList) {
			RichWord richWord = new RichWord(str);
			if (binarySearch(str.toLowerCase()))
				richWord.setCorrect(true);
			else
				richWord.setCorrect(false);
			parole.add(richWord);
		}

		return parole;
	}
	

	/**
	 * helper function per implementare la ricerca dicotomica
	 * @param stemp
	 * @return true se la stringa è stata trovata, false altrimenti
	 */
	private boolean binarySearch(String stemp) {
		int inizio = 0;
		int fine = dizionario.size();

		while (inizio != fine) {
			int medio = inizio + (fine - inizio) / 2;
			if (stemp.compareToIgnoreCase(dizionario.get(medio)) == 0) {
				return true;
			} else if (stemp.compareToIgnoreCase(dizionario.get(medio)) > 0) {
				inizio = medio + 1;
			} else {
				fine = medio;
			}
		}

		return false;
	}
	
	
	/**
	 * helper function per inizializzare la lista di parole controllate secondo il tipo di array scelto
	 * @param type, il tipo di lista da restituire
	 * @return una lista di RichWord
	 */
	private List<RichWord> initializeListType(ArrayType type){
		List<RichWord> parole;
		switch (type){
			case ArrayList:{
				parole = new ArrayList<RichWord>();
				break;
			}
			case LinkedList:{
				parole = new LinkedList<RichWord>();
				break;
			}
			default:{
				parole = new ArrayList<RichWord>();
				break;
			}
		}
		return parole;
	}
	
	
	/**
	 * helper function per inizializzare la lista del dizionario secondo il tipo di array scelto
	 * @param type, il tipo di lista da restituire
	 * @return una lista di String
	 */
	private List<String> initializeDictionaryListType(ArrayType type){
		List<String> dizionario;
		switch (type){
			case ArrayList:{
				dizionario = new ArrayList<String>();
				break;
			}
			case LinkedList:{
				dizionario = new LinkedList<String>();
				break;
			}
			default:{
				dizionario = new ArrayList<String>();
				break;
			}
		}
		return dizionario;
	}
	

}
