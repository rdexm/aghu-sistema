package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioOrdenacaoRelatorioPacientesUnidade implements Dominio {
	/*
	 * Ordenação por leito
	 */
	LEITO,
	/*
	 * Ordenação por nome
	 */
	NOME;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case LEITO:
			return "Leito";
		case NOME:
			return "Nome";
		default:
			return "";
		}	
	}
}
