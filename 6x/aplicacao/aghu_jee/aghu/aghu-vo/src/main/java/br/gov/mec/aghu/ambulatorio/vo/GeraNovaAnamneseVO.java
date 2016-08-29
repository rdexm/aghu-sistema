package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.MamAnamneses;


public class GeraNovaAnamneseVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5225810225588372895L;
	private MamAnamneses anamnese;
	private Boolean replicaAnamnese;
	private Boolean executaPreGera;
	
	public MamAnamneses getAnamnese() {
		return anamnese;
	}
	public void setAnamnese(MamAnamneses anamnese) {
		this.anamnese = anamnese;
	}
	public Boolean getReplicaAnamnese() {
		return replicaAnamnese;
	}
	public void setReplicaAnamnese(Boolean replicaAnamnese) {
		this.replicaAnamnese = replicaAnamnese;
	}
	public Boolean getExecutaPreGera() {
		return executaPreGera;
	}
	public void setExecutaPreGera(Boolean executaPreGera) {
		this.executaPreGera = executaPreGera;
	}
	
}
