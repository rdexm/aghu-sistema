package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;

public class AtendPacExternPorColetasRealizadasVO {

	private Integer seq;
	private Date dthrProgramada;
	private Short seqp;
	private Integer matricula;
	private Short vinCodigo;
	
	
	
	
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public Date getDthrProgramada() {
		return dthrProgramada;
	}
	public void setDthrProgramada(Date dthrProgramada) {
		this.dthrProgramada = dthrProgramada;
	}
	public Short getSeqp() {
		return seqp;
	}
	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
	public Integer getMatricula() {
		return matricula;
	}
	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}
	public Short getVinCodigo() {
		return vinCodigo;
	}
	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}
	
}
