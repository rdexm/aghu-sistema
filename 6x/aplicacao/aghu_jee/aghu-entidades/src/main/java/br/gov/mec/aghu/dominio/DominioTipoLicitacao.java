package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o tipo de licitação
 * 
 * @author agerling
 * 
 */
public enum DominioTipoLicitacao implements Dominio {
	
	/**
	 * Compra
	 */
	CP,

	/**
	 * Registro de Preço
	 */
	RP;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case CP:
			return "Compra";
		case RP:
			return "Registro de Preço";
		default:
			return "";
		}
	}

}
