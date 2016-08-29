package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;

public class RelatorioExamesPacientesListaObservacaoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7453872180601296515L;
	
	private String codigoObservacao;
	private String descricaoObservacao;
	
	public String getCodigoObservacao() {
		return codigoObservacao;
	}
	public void setCodigoObservacao(String codigoObservacao) {
		this.codigoObservacao = codigoObservacao;
	}
	public String getDescricaoObservacao() {
		return descricaoObservacao;
	}
	public void setDescricaoObservacao(String descricaoObservacao) {
		this.descricaoObservacao = descricaoObservacao;
	}
	

}
