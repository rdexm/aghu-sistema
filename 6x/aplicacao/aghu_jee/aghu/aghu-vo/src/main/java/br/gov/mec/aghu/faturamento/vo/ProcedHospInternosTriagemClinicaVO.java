package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;

public class ProcedHospInternosTriagemClinicaVO {

	
	private Date dthrConsistenciaOk;;
	private Integer phiSeq;
	private Integer serMatricula;
	private Integer serVinCodigo;
	private Short unfSeq;
	private Integer pacCodigo;
	private Long trgSeq;
	private Short seqp;
	private Long txaTrgSeq;
	private Short txaSeqp;
	
		
	
	
	public Date getDthrConsistenciaOk() {
		return dthrConsistenciaOk;
	}
	public void setDthrConsistenciaOk(Date dthrConsistenciaOk) {
		this.dthrConsistenciaOk = dthrConsistenciaOk;
	}
	
	public Integer getSerMatricula() {
		return serMatricula;
	}
	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}
	public Integer getSerVinCodigo() {
		return serVinCodigo;
	}
	public void setSerVinCodigo(Integer serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}
	public Short getUnfSeq() {
		return unfSeq;
	}
	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public Long getTrgSeq() {
		return trgSeq;
	}
	public void setTrgSeq(Long trgSeq) {
		this.trgSeq = trgSeq;
	}
	public Short getSeqp() {
		return seqp;
	}
	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
	public Integer getPhiSeq() {
		return phiSeq;
	}
	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}
	public void setTxaTrgSeq(Long txaTrgSeq) {
		this.txaTrgSeq = txaTrgSeq;
	}
	public Long getTxaTrgSeq() {
		return txaTrgSeq;
	}
	public void setTxaSeqp(Short txaSeqp) {
		this.txaSeqp = txaSeqp;
	}
	public Short getTxaSeqp() {
		return txaSeqp;
	}
	

}
