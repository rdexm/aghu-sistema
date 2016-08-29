package br.gov.mec.aghu.paciente.vo;

import java.io.Serializable;

public class ProfissaoVO implements Serializable {
	
/**
	 * 
	 */
	private static final long serialVersionUID = -471998023927530058L;

private Integer codigoOcupacao;
	
	private String descricao;
	
	public ProfissaoVO(Integer codigoOcupacao, String descricao) {
		this.codigoOcupacao = codigoOcupacao;
		this.descricao = descricao;
	}
	
	public Integer getCodigoOcupacao() {
		return codigoOcupacao;
	}

	public void setCodigoOcupacao(Integer codigoOcupacao) {
		this.codigoOcupacao = codigoOcupacao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
