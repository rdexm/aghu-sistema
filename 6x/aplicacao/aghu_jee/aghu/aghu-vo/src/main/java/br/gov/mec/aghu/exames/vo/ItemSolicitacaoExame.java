package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;

/**
 * VO que representa o id de um Item de Solicitação de Exame
 * 
 * @author luismoura
 * 
 */
public class ItemSolicitacaoExame implements Serializable {
	private static final long serialVersionUID = -92569821747730258L;

	private Integer soeSeq;
	private Short seqp;

	public ItemSolicitacaoExame() {

	}

	public ItemSolicitacaoExame(Integer soeSeq, Short seqp) {
		this.soeSeq = soeSeq;
		this.seqp = seqp;
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
