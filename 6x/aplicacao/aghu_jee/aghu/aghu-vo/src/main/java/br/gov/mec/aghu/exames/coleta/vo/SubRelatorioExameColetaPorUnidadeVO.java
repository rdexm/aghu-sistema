package br.gov.mec.aghu.exames.coleta.vo;

import java.io.Serializable;
import java.util.Date;

public class SubRelatorioExameColetaPorUnidadeVO implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2047178099786822060L;
	private String origem;
	private Integer solicitacao;
	private String nomeUsualMaterial;
	private Short unfSeq;
	private Date dtHrEvento;
	
	
	public SubRelatorioExameColetaPorUnidadeVO() {
		super();
	}

	public SubRelatorioExameColetaPorUnidadeVO(String origem,
			Integer solicitacao, String nomeUsualMaterial, Short unfSeq,
			Date dtHrEvento) {
		super();
		this.origem = origem;
		this.solicitacao = solicitacao;
		this.nomeUsualMaterial = nomeUsualMaterial;
		this.unfSeq = unfSeq;
		this.dtHrEvento = dtHrEvento;
	}

	public String getOrigem() {
		return origem;
	}
	public void setOrigem(String origem) {
		this.origem = origem;
	}
	public Integer getSolicitacao() {
		return solicitacao;
	}
	public void setSolicitacao(Integer solicitacao) {
		this.solicitacao = solicitacao;
	}
	public String getNomeUsualMaterial() {
		return nomeUsualMaterial;
	}
	public void setNomeUsualMaterial(String nomeUsualMaterial) {
		this.nomeUsualMaterial = nomeUsualMaterial;
	}
	public Short getUnfSeq() {
		return unfSeq;
	}
	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}
	public Date getDtHrEvento() {
		return dtHrEvento;
	}
	public void setDtHrEvento(Date dtHrEvento) {
		this.dtHrEvento = dtHrEvento;
	}
	
}