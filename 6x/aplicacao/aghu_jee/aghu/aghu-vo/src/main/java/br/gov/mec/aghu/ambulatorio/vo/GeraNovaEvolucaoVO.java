package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.MamEvolucoes;


public class GeraNovaEvolucaoVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7179350485113235258L;
	private MamEvolucoes evolucao;
	private Boolean replicaEvolucao;
	private Boolean executaPreGera;
	
	public MamEvolucoes getEvolucao() {
		return evolucao;
	}
	public void setEvolucao(MamEvolucoes evolucao) {
		this.evolucao = evolucao;
	}
	public Boolean getReplicaEvolucao() {
		return replicaEvolucao;
	}
	public void setReplicaEvolucao(Boolean replicaEvolucao) {
		this.replicaEvolucao = replicaEvolucao;
	}
	public Boolean getExecutaPreGera() {
		return executaPreGera;
	}
	public void setExecutaPreGera(Boolean executaPreGera) {
		this.executaPreGera = executaPreGera;
	}

}
