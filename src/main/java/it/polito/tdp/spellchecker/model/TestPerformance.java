package it.polito.tdp.spellchecker.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class TestPerformance {

	public static void main(String[] args) {
				
		final int NUMBER_OF_TESTS = 10;
		final Dictionary.ArrayType arrayType = Dictionary.ArrayType.ArrayList;

		Dictionary model = new Dictionary();
		try {
			model.loadDictionary(Dictionary.Languages.Italian, arrayType);
		}catch(IllegalArgumentException e) {
    		System.err.println("Seleziona una lingua!");
     		return;
    	}catch(IOException e) {
    		System.err.println("Errore nella lettura del dizionario");
    		return;
    	}

		String inputText = "In informatica Java è un linguaggio di programmazione ad alto livello, "
				+ "orientato agli oggetti e a tipizzazione statica, specificatamente progettato per essere "
				+ "il più possibile indipendente dalla piattaforma di esecuzione.";
		System.out.println(inputText);
		List<String> inputTextList = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(inputText, "\n[.,?\\/#!$%^&*;:{}=-_`~()] ");
		while (st.hasMoreTokens()) {
			inputTextList.add(st.nextToken());
		}

		List<Double> t1 = new ArrayList<>();
		List<Double> t2 = new ArrayList<>();
		List<Double> t3 = new ArrayList<>();

		for (int test = 0; test < NUMBER_OF_TESTS; test++) {

			long start = System.nanoTime();
			model.spellCheckText(inputTextList, arrayType);
			long end = System.nanoTime();
			t1.add((end - start) / 10e6);

			start = System.nanoTime();
			model.spellCheckTextLinear(inputTextList, arrayType);
			end = System.nanoTime();
			t2.add((end - start) / 10e6);

			start = System.nanoTime();
			model.spellCheckTextDichotomic(inputTextList, arrayType);
			end = System.nanoTime();
			t3.add((end - start) / 10e6);
		}

		System.out.print("\n");
		System.out.println("debug cont: " + t1);
		System.out.println("debug line: " + t2);
		System.out.println("debug dich: " + t3);
		System.out.print("\n");
		
		System.out.println(String.format("%-30s %10f %-2s (AVG on %d runs)", "CONTAINS took: ",
				calculateAverage(t1), "ms", NUMBER_OF_TESTS));
		System.out.println(String.format("%-30s %10f %-2s (AVG on %d runs)", "LINEAR SEARCH took: ",
				calculateAverage(t2), "ms", NUMBER_OF_TESTS));
		System.out.println(String.format("%-30s %10f %-2s (AVG on %d runs)", "DICHOTOMIC SEARCH took: ",
				calculateAverage(t3), "ms", NUMBER_OF_TESTS));

	}
	
	private static double calculateAverage(List<Double> marks) {
		Double sum = 0.0;
		if (!marks.isEmpty()) {
			for (Double mark : marks) {
				sum += mark;
			}
			return sum.doubleValue() / marks.size();
		}
		return sum;
	}

}
