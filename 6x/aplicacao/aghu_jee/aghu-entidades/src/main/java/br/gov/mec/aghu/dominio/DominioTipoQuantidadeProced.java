package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioTipoQuantidadeProced implements Dominio {
	
	V("Valor fixo informado no atributo QUANTIDADE"),
	P("Quantidade do Procedimento que gerou o ato obrigatório"),
	D("Número de Diárias da internação"),
	;
	
	private final String descricao; 
	
	private DominioTipoQuantidadeProced(String descricao) {

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
