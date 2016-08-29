package br.gov.mec.aghu.paciente.vo;

public class InclusaoAtendimentoVO {
	
	private Integer atendimentoSeq;
	
	private String leitoId;
	
	private Short numeroQuarto;
	
	private Short seqUnidadeFuncional;
	
	public InclusaoAtendimentoVO() {
	}
	
	public InclusaoAtendimentoVO(String leitoId, Short numeroQuarto, Short seqUnidadeFuncional, Integer atdSeq) {
		this.leitoId = leitoId;
		this.numeroQuarto = numeroQuarto;
		this.seqUnidadeFuncional = seqUnidadeFuncional;
		this.atendimentoSeq = atdSeq;
	}
	
	public String getLeitoId() {
		return leitoId;
	}

	public void setLeitoId(String leitoId) {
		this.leitoId = leitoId;
	}

	public Short getNumeroQuarto() {
		return numeroQuarto;
	}

	public void setNumeroQuarto(Short numeroQuarto) {
		this.numeroQuarto = numeroQuarto;
	}

	public Short getSeqUnidadeFuncional() {
		return seqUnidadeFuncional;
	}

	public void setSeqUnidadeFuncional(Short seqUnidadeFuncional) {
		this.seqUnidadeFuncional = seqUnidadeFuncional;
	}

	/**
	 * @param atendimentoSeq the atendimentoSeq to set
	 */
	public void setAtendimentoSeq(Integer atendimentoSeq) {
		this.atendimentoSeq = atendimentoSeq;
	}

	/**
	 * @return the atendimentoSeq
	 */
	public Integer getAtendimentoSeq() {
		return atendimentoSeq;
	}
	
}
