package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioOrigemProcedimentoAmbulatorialRealizado implements Dominio {
	
	/**
	 * Digitado
	 */
	DIG, 
	
	/**
	 * Exame
	 */
	EXA, 
	
	/**
	 * Consulta
	 */
	CON, 
	
	/**
	 * Banco Sangue
	 */
	BSA, 
	
	/**
	 * Cirurgia Ambulatório
	 */
	CIA, 
	
	/**
	 * Internação
	 */
	AIN, 
	
	/**
	 * Triagem
	 */
	TRG;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case DIG:
			return "Digitado";
		case EXA:
			return "Exame";
		case CON:
			return "Consulta";
		case BSA:
			return "Banco Sangue";
		case CIA:
			return "Cirurgia Ambulatório";
		case AIN:
			return "Internação";
		case TRG:
			return "Triagem";
		default:
			return "";
		}
	}
	
}
