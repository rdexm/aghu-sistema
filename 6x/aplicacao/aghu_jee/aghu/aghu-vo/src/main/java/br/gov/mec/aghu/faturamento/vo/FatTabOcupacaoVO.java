package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;


public class FatTabOcupacaoVO implements Serializable {

	private static final long serialVersionUID = 4528174123371025541L;

	private String cboOcupacao;
	
	private String cboDescricao;

	private String descricao;

	public String getCboOcupacao() {
		return cboOcupacao;
	}

	public void setCboOcupacao(String cboOcupacao) {
		this.cboOcupacao = cboOcupacao;
	}

	public String getCboDescricao() {
		return cboDescricao;
	}

	public void setCboDescricao(String cboDescricao) {
		this.cboDescricao = cboDescricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	
}
