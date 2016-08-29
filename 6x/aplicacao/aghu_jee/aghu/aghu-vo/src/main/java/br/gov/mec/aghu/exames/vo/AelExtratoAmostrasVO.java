package br.gov.mec.aghu.exames.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;

public class AelExtratoAmostrasVO {
	
	private Integer amoSoeSeq;
	private Short amoSeqp;
	private Short seqp;
	private DominioSituacaoAmostra situacao;
	private Date criadoEm;
	private String servidorResponsavel;
	public Integer getAmoSoeSeq() {
		return amoSoeSeq;
	}
	public void setAmoSoeSeq(Integer amoSoeSeq) {
		this.amoSoeSeq = amoSoeSeq;
	}
	public Short getAmoSeqp() {
		return amoSeqp;
	}
	public void setAmoSeqp(Short amoSeqp) {
		this.amoSeqp = amoSeqp;
	}
	public Short getSeqp() {
		return seqp;
	}
	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
	public DominioSituacaoAmostra getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSituacaoAmostra situacao) {
		this.situacao = situacao;
	}
	public Date getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	public String getServidorResponsavel() {
		return servidorResponsavel;
	}
	public void setServidorResponsavel(String servidorResponsavel) {
		this.servidorResponsavel = servidorResponsavel;
	}

	
}
