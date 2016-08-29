package br.gov.mec.aghu.internacao.vo;

import java.io.Serializable;


public class IntercorrenciaAtualVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4791251480022598346L;

	
	private String descricaoInterAtual;
	private String complementoInterAtual;
	
	
	public String getDescricaoInterAtual() {
		return descricaoInterAtual;
	}
	public void setDescricaoInterAtual(String descricaoInterAtual) {
		this.descricaoInterAtual = descricaoInterAtual;
	}
	public String getComplementoInterAtual() {
		return complementoInterAtual;
	}
	public void setComplementoInterAtual(String complementoInterAtual) {
		this.complementoInterAtual = complementoInterAtual;
	}

	
}
