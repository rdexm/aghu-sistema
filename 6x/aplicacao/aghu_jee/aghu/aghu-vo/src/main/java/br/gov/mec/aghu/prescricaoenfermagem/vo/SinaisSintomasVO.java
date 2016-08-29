package br.gov.mec.aghu.prescricaoenfermagem.vo;

import java.io.Serializable;

public class SinaisSintomasVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -704436150870907274L;

	private Short seq;
	private String descricao;
	private Boolean selecionada;
	
	public SinaisSintomasVO(){
		
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Boolean getSelecionada() {
		return selecionada;
	}

	public void setSelecionada(Boolean selecionada) {
		this.selecionada = selecionada;
	}

}
