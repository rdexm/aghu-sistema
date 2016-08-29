package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoTransplante implements Dominio {

	/**
	 * A????
	 */
	A,

	/**
	 * Em Espera
	 */
	E, 
	
	/**
	 * I????
	 */
	I,
	
	/**
	 * S????
	 */
	S,
	
	/**
	 * T????
	 */
	T,
	/** 
	Retirada Lista 
	**/
	R;
	

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "A";
		case E:
			return "Em Espera";
		case I:
			return "I";
		case S:
			return "S";
		case T:
			return "T";
		case R:
			return "R";
		default:
			return "";
		}
	}
	
	public String retornarDescricaoCompleta(){
		switch (this) {
		case A:
			return "Aguardando Doador";
		case E:
			return "Aguardando Transplante";
		case T:
			return "Transplantado";
		case I:
			return "Inativo";
		case S:
			return "Stand By";
		case R:
			return "Retirado da Lista";
		default:
			return "";
		}
	}
}
