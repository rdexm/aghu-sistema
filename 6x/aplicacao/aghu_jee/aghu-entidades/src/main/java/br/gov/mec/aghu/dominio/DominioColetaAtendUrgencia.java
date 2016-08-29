package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author lsamberg
 *
 */
public enum DominioColetaAtendUrgencia implements Dominio {

	/**
	 * Só Plantão
	 */
	P,
	
	/**
	 * Todo Dia 
	 */
	T;


	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "Só Plantão";
		case T:
			return "Todo Dia";
		default:
			return "";
		}
	}
	
}
