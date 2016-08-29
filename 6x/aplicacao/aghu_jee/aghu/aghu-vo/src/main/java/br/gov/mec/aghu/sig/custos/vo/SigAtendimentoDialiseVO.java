package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;
import java.util.Date;

public class SigAtendimentoDialiseVO extends SigAtendimentoVO {
	private Boolean indPacPediatrico;
	private Date dtDispensacao;
	private Short frequencia;
	private Short tfqSeq;
	private String vadSigla;
	private Integer medMat;
	private BigDecimal quantidadeSolicitada;
	private BigDecimal quantidadeDispensada;
	
	public SigAtendimentoDialiseVO(){}
	
	public Boolean getIndPacPediatrico() {
		return indPacPediatrico;
	}
	public void setIndPacPediatrico(Boolean indPacPediatrico) {
		this.indPacPediatrico = indPacPediatrico;
	}
	public Date getDtDispensacao() {
		return dtDispensacao;
	}
	public void setDtDispensacao(Date dtDispensacao) {
		this.dtDispensacao = dtDispensacao;
	}
	public Short getFrequencia() {
		return frequencia;
	}
	public void setFrequencia(Short frequencia) {
		this.frequencia = frequencia;
	}
	public Short getTfqSeq() {
		return tfqSeq;
	}
	public void setTfqSeq(Short tfqSeq) {
		this.tfqSeq = tfqSeq;
	}
	public String getVadSigla() {
		return vadSigla;
	}
	public void setVadSigla(String vadSigla) {
		this.vadSigla = vadSigla;
	}
	public Integer getMedMat() {
		return medMat;
	}
	public void setMedMat(Integer medMat) {
		this.medMat = medMat;
	}
	public BigDecimal getQuantidadeSolicitada() {
		return quantidadeSolicitada;
	}
	public void setQuantidadeSolicitada(BigDecimal quantidadeSolicitada) {
		this.quantidadeSolicitada = quantidadeSolicitada;
	}
	public BigDecimal getQuantidadeDispensada() {
		return quantidadeDispensada;
	}
	public void setQuantidadeDispensada(BigDecimal quantidadeDispensada) {
		this.quantidadeDispensada = quantidadeDispensada;
	}
}
