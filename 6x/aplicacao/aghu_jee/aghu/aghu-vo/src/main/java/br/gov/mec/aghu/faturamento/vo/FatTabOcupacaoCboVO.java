package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.util.Date;


public class FatTabOcupacaoCboVO implements Serializable {

	private static final long serialVersionUID = 499345618339010614L;

	private Integer cboSeq;
	
	private String cbo;

	private String descricao;
	
	private Date dtInicio;
	
	private Date dtFim;
	
	private Boolean processado;

	public Integer getCboSeq() {
		return cboSeq;
	}

	public void setCboSeq(Integer cboSeq) {
		this.cboSeq = cboSeq;
	}

	public String getCbo() {
		return cbo;
	}

	public void setCbo(String cbo) {
		this.cbo = cbo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Date getDtInicio() {
		return dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	public Date getDtFim() {
		return dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	public Boolean getProcessado() {
		return processado;
	}

	public void setProcessado(Boolean processado) {
		this.processado = processado;
	}

		
}
