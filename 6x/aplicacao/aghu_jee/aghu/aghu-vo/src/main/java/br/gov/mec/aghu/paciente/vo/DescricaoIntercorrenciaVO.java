package br.gov.mec.aghu.paciente.vo;

import java.io.Serializable;

public class DescricaoIntercorrenciaVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6509827503964423407L;
	
	String descricao;
	String complemento;
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public String getComplemento() {
		return complemento;
	}
	
	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
	
}
