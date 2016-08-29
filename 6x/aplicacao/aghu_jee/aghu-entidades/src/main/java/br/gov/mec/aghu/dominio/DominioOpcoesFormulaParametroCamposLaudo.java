package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioOpcoesFormulaParametroCamposLaudo implements Dominio {

	/**
	 * Idade
	 */
	IDADE, 

	/**
	 * Sexo
	 */
	SEXO,
	
	/**
	 * Potência
	 */
	POWER,
	
	/**
	 * Raiz
	 */
	RAIZ,
	
	/**
	 * Decode
	 */
	DECODE,
	
	/**
	 * Greatest
	 */
	GREATEST,
	
	/**
	 * Sign
	 */
	SIGN,
	
	/**
	 * Adição
	 */
	ADICAO,
	
	/**
	 * Subtração
	 */
	SUBTRACAO,
	
	/**
	 * Multiplicação
	 */
	MULTIPLICACAO,
	
	/**
	 * Divisão
	 */
	DIVISAO,
	
	/**
	 * Abre parênteses
	 */
	ABRE_PARENTESES,
	
	/**
	 * Fecha parênteses
	 */
	FECHA_PARENTESES;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case IDADE:
			return "Idade";
		case SEXO:
			return "Sexo";
		case POWER:
			return "Potência";
		case RAIZ:
			return "Raiz";
		case DECODE:
			return "Decode";
		case GREATEST:
			return "Greatest";
		case SIGN:
			return "Sign";
		case ADICAO:
			return "Adição";
		case SUBTRACAO:
			return "Subtração";
		case MULTIPLICACAO:
			return "Multiplicação";
		case DIVISAO:
			return "Divisão";
		case ABRE_PARENTESES:
			return "Abre Parênteses";
		case FECHA_PARENTESES:
			return "Fecha Parênteses";
		default:
			return "";
		}
	}
	
	
	public static DominioOpcoesFormulaParametroCamposLaudo getInstance(String opcao) {
		
		if ("idade".equalsIgnoreCase(opcao)) {
			return DominioOpcoesFormulaParametroCamposLaudo.IDADE;
		} else if ("sexo".equalsIgnoreCase(opcao)) {
			return DominioOpcoesFormulaParametroCamposLaudo.SEXO;
		} else if ("power".equalsIgnoreCase(opcao)) {
			return DominioOpcoesFormulaParametroCamposLaudo.POWER;
		} else if ("raiz".equalsIgnoreCase(opcao)) {
			return DominioOpcoesFormulaParametroCamposLaudo.RAIZ;
		} else if ("decode".equalsIgnoreCase(opcao)) {
			return DominioOpcoesFormulaParametroCamposLaudo.DECODE;
		} else if ("greatest".equalsIgnoreCase(opcao)) {
			return DominioOpcoesFormulaParametroCamposLaudo.GREATEST;
		} else if ("sign".equalsIgnoreCase(opcao)) {
			return DominioOpcoesFormulaParametroCamposLaudo.SIGN;
		} else if ("adicao".equalsIgnoreCase(opcao)) {
			return DominioOpcoesFormulaParametroCamposLaudo.ADICAO;
		} else if ("subtracao".equalsIgnoreCase(opcao)) {
			return DominioOpcoesFormulaParametroCamposLaudo.SUBTRACAO;
		} else if ("multiplicacao".equalsIgnoreCase(opcao)) {
			return DominioOpcoesFormulaParametroCamposLaudo.MULTIPLICACAO;
		} else if ("divisao".equalsIgnoreCase(opcao)) {
			return DominioOpcoesFormulaParametroCamposLaudo.DIVISAO;
		} else if ("abre_parenteses".equalsIgnoreCase(opcao)) {
			return DominioOpcoesFormulaParametroCamposLaudo.ABRE_PARENTESES;
		} else if ("fecha_parenteses".equalsIgnoreCase(opcao)) {
			return DominioOpcoesFormulaParametroCamposLaudo.FECHA_PARENTESES;
		}else{
			return null;
		}
	}
}