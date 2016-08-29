package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o tipo de pesquisa que é feito na solicitação de exames
 */
public enum DominioTipoPesquisaExame implements Dominio {
	
	/**
	 * Pesquisa com like somente a direita
	 */
	INICIO,
	
	/**
	 * Pesquisa com like em ambos os lados
	 */
	QUALQUER;

	private int value;

	private DominioTipoPesquisaExame() {
	}
	
	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case INICIO:
			return "Pesquisar pelo início do exame";
		case QUALQUER:
			return "Pesquisar por qualquer parte do exame";
		default:
			return "";
		}
	}

}
