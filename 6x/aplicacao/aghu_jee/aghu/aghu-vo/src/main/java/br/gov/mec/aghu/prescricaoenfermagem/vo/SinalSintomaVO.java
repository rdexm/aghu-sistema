package br.gov.mec.aghu.prescricaoenfermagem.vo;

import java.io.Serializable;

public class SinalSintomaVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -704436150870907274L;

	private Integer codigo;
	private String descricao;
	private Boolean selecionado;
	
	public SinalSintomaVO(){
		
	}

	
	public Integer getCodigo() {
		return codigo;
	}


	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}


	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

}
