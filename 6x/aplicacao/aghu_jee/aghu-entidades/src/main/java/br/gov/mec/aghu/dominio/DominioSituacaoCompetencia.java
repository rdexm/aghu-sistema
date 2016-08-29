package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoCompetencia implements Dominio {
	
	A("Aberta"),
	M("Manutencao"),
	P("Apresentada"),
	;
	
	private final String descricao;
	
	private DominioSituacaoCompetencia(String descricao) {
					
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