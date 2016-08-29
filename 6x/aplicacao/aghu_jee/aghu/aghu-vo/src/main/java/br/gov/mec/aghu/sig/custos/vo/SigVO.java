package br.gov.mec.aghu.sig.custos.vo;

import java.util.Date;

public class SigVO {
	
	private Integer atdPaciente;
	private Integer pacCodigo;
	private Integer trpSeq;
	private Date dtPrevExecucao;
	private Integer centroCusto;
	private Integer itoPtoSeq;
	private Short itoSeqp;
	
	public SigVO(){}
	
	public SigVO(Integer atdPaciente, Integer pacCodigo, Integer trpSeq,
			Date dtPrevExecucao, Integer centroCusto) {
		super();
		this.atdPaciente = atdPaciente;
		this.pacCodigo = pacCodigo;
		this.trpSeq = trpSeq;
		this.dtPrevExecucao = dtPrevExecucao;
		this.centroCusto = centroCusto;
	}	
	
	public Integer getAtdPaciente() {
		return atdPaciente;
	}
	public void setAtdPaciente(Integer atdPaciente) {
		this.atdPaciente = atdPaciente;
	}
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public Integer getTrpSeq() {
		return trpSeq;
	}
	public void setTrpSeq(Integer trpSeq) {
		this.trpSeq = trpSeq;
	}
	public Date getDtPrevExecucao() {
		return dtPrevExecucao;
	}
	public void setDtPrevExecucao(Date dtPrevExecucao) {
		this.dtPrevExecucao = dtPrevExecucao;
	}
	public Integer getCentroCusto() {
		return centroCusto;
	}
	public void setCentroCusto(Integer centroCusto) {
		this.centroCusto = centroCusto;
	}

	public void setItoPtoSeq(Integer itoPtoSeq) {
		this.itoPtoSeq = itoPtoSeq;
	}

	public Integer getItoPtoSeq() {
		return itoPtoSeq;
	}

	public void setItoSeqp(Short itoSeqp) {
		this.itoSeqp = itoSeqp;
	}

	public Short getItoSeqp() {
		return itoSeqp;
	}
}
