package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoCompProd implements Dominio {

	A("Aberta"), F("Fechada");

	private final String descricao;

	private DominioSituacaoCompProd(String descricao) {

		this.descricao = descricao;
	}

	@Override
	public int getCodigo() {

		return this.ordinal();
	}

	@Override
	public String getDescricao() {

		return this.descricao;
	}
}