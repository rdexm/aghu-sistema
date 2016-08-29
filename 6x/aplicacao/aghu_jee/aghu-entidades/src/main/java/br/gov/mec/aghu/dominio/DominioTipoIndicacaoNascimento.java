package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio para o tipo de indicacao de nascimento.
 * 
 */
public enum DominioTipoIndicacaoNascimento implements Dominio {

	/**
	 * Cesariana
	 */
    C,
	/**
	 * Instrumentado
	 */
     F,
	/**
	 * Cesariana e Instrumentado
	 */
    A,
	/**
	 * Parto
	 */
	P,
	/**
	 * Parto e Cesárea
	 */
	B;
	
   
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case C:
			return "Cesariana";
		case F:
			return "Instrumentado";
		case A:
			return "Cesariana e Instrumentado";
		case P:
			return "Parto";
		case B:
			return "Parto e Cesárea";
		default:
			return "";
		}
	}
}
