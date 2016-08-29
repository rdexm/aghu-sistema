package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio que mapeia o tipos de relatórios a serem impressos.
 * 
 * @author Fábio Szymanski Winck
 * 
 */
public enum DominioTipoImpressaoLaudo implements Dominio {

	LAUDO_SAMIS,
	LAUDO_PAC_EXTERNO;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		
		switch (this) {
		case LAUDO_SAMIS:
			return "Impressão SAMIS";
		case LAUDO_PAC_EXTERNO:
			return "Impressão Paciente Externo";
		default:
			return this.toString();	
		}
	}
}