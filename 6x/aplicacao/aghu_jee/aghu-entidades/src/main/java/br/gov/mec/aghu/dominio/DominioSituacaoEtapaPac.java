package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoEtapaPac implements Dominio {
	
	/**
	 * Em Atendimento
	 */
	AT("AT", "Em Atendimento"),
	
	/**
	 * Pendente
	 */
	PD("PD", "Pendente"),
	
	/**
	 * Rejeitada
	 */
	RJ("RJ", "Rejeitada"),
	
	/**
	 * Realizada
	 */
	RZ("RZ", "Realizada");
	
	private DominioSituacaoEtapaPac(String codigo, String descricao){
		this.descricao = descricao;
		this.codigo = codigo;
	}
	
	private String codigo;
	private String descricao;
	
	@Override
	public String getDescricao() {
		return descricao;
	}

	@Override
	public int getCodigo() {		
		return this.ordinal();
	}
	
	@Override
	public String toString() {
		return codigo;
	}

}
