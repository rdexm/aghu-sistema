package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.Date;


public class GeraDisponiblidadeVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5350213618058732917L;
	private Integer qtdGerada;
	private Date dtUltimaGeracao;
	
	public Integer getQtdGerada() {
		return qtdGerada;
	}
	public void setQtdGerada(Integer qtdGerada) {
		this.qtdGerada = qtdGerada;
	}
	public Date getDtUltimaGeracao() {
		return dtUltimaGeracao;
	}
	public void setDtUltimaGeracao(Date dtUltimaGeracao) {
		this.dtUltimaGeracao = dtUltimaGeracao;
	}
	
}
