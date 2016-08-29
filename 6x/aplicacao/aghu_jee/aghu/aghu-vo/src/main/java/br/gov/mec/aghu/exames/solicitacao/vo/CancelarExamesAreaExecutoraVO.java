package br.gov.mec.aghu.exames.solicitacao.vo;

import br.gov.mec.aghu.dominio.DominioSimNao;

public class CancelarExamesAreaExecutoraVO {
	

	private DominioSimNao indCancelaAutomatico;
	private Integer soeSeq;
	private Short seqp;
	
	
	public DominioSimNao getIndCancelaAutomatico() {
		return indCancelaAutomatico;
	}
	public void setIndCancelaAutomatico(DominioSimNao indCancelaAutomatico) {
		this.indCancelaAutomatico = indCancelaAutomatico;
	}
	public Integer getSoeSeq() {
		return soeSeq;
	}
	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}
	public Short getSeqp() {
		return seqp;
	}
	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
	
	

	

}
