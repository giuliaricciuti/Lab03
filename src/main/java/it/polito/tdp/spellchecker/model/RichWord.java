
package it.polito.tdp.spellchecker.model;

/**
 * 
 * @author Carlo Masone
 * Semplice classe wrapper, utilizzata per rappresentare una parola e l'indicazione se tale parola è corretta
 */
public class RichWord {
	
	private String word;		// la parola
	private boolean correct;	// indicazione se la parola sia corretta
	
	
	/**
	 * Costruttore
	 * @param word la parola
	 * @param correct un booleano che rappresenta il fatto che la parola sia corretta (true) o meno (false)
	 */
	public RichWord(String word, boolean correct) {
		this.word = word;
		this.correct = correct;
	}

	public RichWord(String word) {
		this.word = word;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public boolean isCorrect() {
		return correct;
	}

	public void setCorrect(boolean correct) {
		this.correct = correct;
	}

	@Override
	public String toString() {
		return "RichWord: [ word =" + word + "\t correct =" + correct + " ]";
	}
	
	

}
