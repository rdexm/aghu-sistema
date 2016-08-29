package br.gov.mec.aghu.util;

public class ItensConverterAnalyzerMain {

	public static void main(String[] args) {
		System.out.println("ItensConverterAnalyzerMain ....");//NOPMD
		
		ItensConverterAnalyzer analyzer = new ItensConverterAnalyzer();
		
		analyzer.execute("br/gov/mec/aghu/action/converter");
		
		
		System.out.println("ItensConverterAnalyzerMain - Feito!");//NOPMD
	}

}
