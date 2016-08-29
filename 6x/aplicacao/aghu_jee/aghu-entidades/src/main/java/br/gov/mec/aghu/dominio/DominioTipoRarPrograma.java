package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o tipo da entidade RarPrograma, se PP, PC ou AT.
 * 
 * @author daniel.silva
 * @since 18/07/2012
 * 
 */
public enum DominioTipoRarPrograma implements Dominio {
	PP,	PC,	AT;


	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case PP:
			return "PP";
		case PC:
			return "PC";
		case AT:
			return "AT";
		default:
			return "";
		}
	}

}
