package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioOrdenacRelatorioSitPacTmo implements Dominio {
	
	ESCORE("Escore"),
	DATA_INGRESSO("Data de Ingresso"),
	NOME("Nome");

	private final String descricao;
	
	DominioOrdenacRelatorioSitPacTmo(String descricao){
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
	
	@Override
	public String toString(){
		return descricao; 
	}
}
