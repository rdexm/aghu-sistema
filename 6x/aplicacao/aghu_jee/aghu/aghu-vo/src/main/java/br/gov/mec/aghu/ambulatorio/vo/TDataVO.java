package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

public class TDataVO implements Serializable {
	
	private static final long serialVersionUID = 7239263958282370039L;
	
	private Integer hierarquia;
	private String dataInicial;
	private String dataFinal;
	private String descricao;
	private Integer conNumero; 
	
	public String getDataInicial() {
		return dataInicial;
	}
	
	public void setDataInicial(String dataInicial) {
		this.dataInicial = dataInicial;
	}
	
	public String getDataFinal() {
		return dataFinal;
	}
	
	public void setDataFinal(String dataFinal) {
		this.dataFinal = dataFinal;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getHierarquia() {
		return hierarquia;
	}
	public void setHierarquia(Integer hierarquia) {
		this.hierarquia = hierarquia;
	}

	public Integer getConNumero() {
		return conNumero;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}
}