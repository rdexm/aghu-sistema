package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio que mapeia o tipos de relatórios a serem impressos.
 * 
 * @author Fábio Szymanski Winck
 * 
 */
public enum DominioSubTipoImpressaoLaudo implements Dominio {

	LAUDO_SISMAMA,
	LAUDO_GERAL;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		
		switch (this) {
		case LAUDO_SISMAMA:
			return "Impressão do laudo SISMAMA";
		case LAUDO_GERAL:
			return "Impressão de laudos gerais";
		default:
			return this.toString();	
		}
	}
}