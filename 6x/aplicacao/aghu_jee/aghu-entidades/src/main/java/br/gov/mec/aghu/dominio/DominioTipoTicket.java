package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o tipo de um ticket.
 * 
 */
public enum DominioTipoTicket implements Dominio {

	ANALISE_TECNICA;


	@Override
	public int getCodigo() {
		return this.ordinal() + 1;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case ANALISE_TECNICA:
			return "Análise Técnica";
		default:
			return "";
		}
	}

}
