package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioAcaoHistoricoWF implements Dominio {
		CRIACAO("Criação"),
		APROVACAO("Aprovação"),
		REJEICAO("Rejeição"),
		CANCELAMENTO("Cancelamento"),		
		CONCLUSAO("Conclusão"), //TODO documentar
		EXECUCAO("Execução");

	private String descricao;

	private DominioAcaoHistoricoWF(String descricao) {
		this.descricao = descricao;
	}
	
	@Override
	public String getDescricao() {
		return descricao;
	}	

	@Override
	public int getCodigo() {		
		return this.ordinal();
	}
}
