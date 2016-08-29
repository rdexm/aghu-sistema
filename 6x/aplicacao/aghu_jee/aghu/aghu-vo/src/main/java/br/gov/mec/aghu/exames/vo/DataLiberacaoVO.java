package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;
import java.util.Date;


public class DataLiberacaoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1441159224676223294L;
	private Date dataLiberacao;

	public Date getDataLiberacao() {
		return dataLiberacao;
	}

	public void setDataLiberacao(Date dataLiberacao) {
		this.dataLiberacao = dataLiberacao;
	}
	
	
	
}
