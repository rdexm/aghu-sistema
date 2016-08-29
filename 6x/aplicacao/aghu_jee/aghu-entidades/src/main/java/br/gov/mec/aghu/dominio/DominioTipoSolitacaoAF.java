package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;



public enum DominioTipoSolitacaoAF implements Dominio {
	
	M("Material"),
	S("Serviço");
	
	private String descricao;
	
	private DominioTipoSolitacaoAF(String descricao) {
		this.descricao = descricao;
	}
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		return descricao;
	}	
}
