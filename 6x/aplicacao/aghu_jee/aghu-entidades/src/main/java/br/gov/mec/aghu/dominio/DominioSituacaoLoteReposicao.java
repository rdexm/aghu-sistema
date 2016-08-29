package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoLoteReposicao implements Dominio {
	/**
	 * Ativa
	 */
	GR,

	/**
	 * Alterado
	 */
	AL,
	
	/**
	 * Excluida
	 */
	EX;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case GR:
			return "Gerada";
		case EX:
			return "Excluida";
		case AL:
			return "Alterado";	
		default:
			return "";
		}
	}

}
