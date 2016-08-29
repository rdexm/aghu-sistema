package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioGrauInstrucaoRap implements Dominio {

	/**
	 * Analfabeto
	 */
	ANALFABETO(1),

	/**
	 * Primário Incompleto
	 */
	PRIMARIO_INCOMPLETO(2),
	
	/**
	 * Prmário Completo
	 */
	PRIMARIO_COMPLETO(3),

	/**
	 * Primeiro Grau Incompleto: 1a a 4a série
	 */
	PRIMEIRO_GRAU_INCOMPLETO_1_A_4(4),

	/**
	 * Primeiro Grau Incompleto: até 5a série completa
	 */
	PRIMEIRO_GRAU_INCOMPLETO_5(5),

	/**
	 * Primeiro Grau Incompleto: 6a a 7a série
	 */
	PRIMEIRO_GRAU_INCOMPLETO_6_A_7(6),
	
	/**
	 * Primeiro Grau Completo
	 */
	PRIMEIRO_GRAU_COMPLETO(7),
		
	/**
	 * Segundo Grau Incompleto: Em andamento
	 */
	SEGUNDO_GRAU_INCOMPLETO_EM_ANDAMENTO(8),

	/**
	 * Segundo Grau Incompleto: Interrompido
	 */
	SEGUNDO_GRAU_INCOMPLETO_INTERROMPIDO(9),

	/**
	 * Segundo Grau Completo
	 */
	SEGUNDO_GRAU_COMPLETO(10),

	/**
	 * Superior Incompleto
	 */
	SUPERIOR_INCOMPLETO(11),

	/**
	 * Superior Completo
	 */
	SUPERIOR_COMPLETO(12),
	
	/**
	 * Indefinido
	 */
	INDEFINIDO(13);
		

	private int value;
	
	private DominioGrauInstrucaoRap(int value) {
		this.value = value;
	}
	
	
	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case ANALFABETO:
			return "Analfabeto";
		case PRIMARIO_INCOMPLETO:
			return "Primário Incompleto";
		case PRIMARIO_COMPLETO:
			return "Primário Completo";
		case PRIMEIRO_GRAU_INCOMPLETO_1_A_4:
			return "Primeiro Grau Incompleto: 1ª a 4ª série";
		case PRIMEIRO_GRAU_INCOMPLETO_5:
			return "Primeiro Grau Incompleto: até 5ª série completa";
		case PRIMEIRO_GRAU_INCOMPLETO_6_A_7:
			return "Primeiro Grau Incompleto: 6ª a 7ª série";
		case PRIMEIRO_GRAU_COMPLETO:
			return "Primeiro Grau Completo";
		case SEGUNDO_GRAU_INCOMPLETO_EM_ANDAMENTO:
			return "Segundo Grau Incompleto: Em andamento";
		case SEGUNDO_GRAU_INCOMPLETO_INTERROMPIDO:
			return "Segundo Grau Incompleto: Interrompido";
		case SEGUNDO_GRAU_COMPLETO:
			return "Segundo Grau Completo";
		case SUPERIOR_INCOMPLETO:
			return "Superior Incompleto";
		case SUPERIOR_COMPLETO:
			return "Superior Completo";
		case INDEFINIDO:
			return "Indefinido";			
		default:
			return "";
		}
	}	
}
