package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioAtualizacaoSaldo implements Dominio {
	
	I("Incrementar"),
	D("Decrementar");
	
	private final String descricao;
	
	private DominioAtualizacaoSaldo(String descricao) {
					
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