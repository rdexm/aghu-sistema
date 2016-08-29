package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.io.Serializable;
import java.util.Date;

public class DiagnosticosVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1271350921358034039L;

	private String descricao;

	private Date dataFim;
	
	private Date data;
	


	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}


	
	

}
