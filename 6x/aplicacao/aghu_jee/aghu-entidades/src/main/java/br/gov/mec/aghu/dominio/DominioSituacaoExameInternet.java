package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoExameInternet implements Dominio {

	/**
	 * Exame Não Realizado 
	 */
	N, 
	/**
	 * Exame Realizado com Sucesso
	 */
	R, 
	/**
	 * Exame Realizado com Erro
	 */
	E;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case N:
			return "Não Realizado";
		case R:
			return "Realizado com Sucesso";
		case E:
			return "Realizado com Erro";			
		default:
			return "";
		}
	}
	
}
