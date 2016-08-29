package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoProducao implements Dominio {

	P("Produção"), D("Diferença");

	private final String descricao;

	private DominioSituacaoProducao(String descricao) {

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