package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o tipo do retorno de agenda numa alta.
 * 
 * @author dansantos
 * 
 */
public enum DominioRetornoAgenda implements Dominio {
	
	/**
	 * Bloqueado
	 */
	B,
	/**
	 * Liberado
	 */
	L;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case B:
			return "Bloqueado";
		case L:
			return "Liberado";
		default:
			return "";
		}
	}

}
