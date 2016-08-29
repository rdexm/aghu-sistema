package br.gov.mec.aghu.paciente.vo;

public class AtualizarLocalAtendimentoVO {
	
	private String leitoId;
	
	private Short numeroQuarto;
	
	private Short seqUnidadeFuncional;
	
	public AtualizarLocalAtendimentoVO() {
	}
	
	public AtualizarLocalAtendimentoVO(String leitoId, Short numeroQuarto, Short seqUnidadeFuncional) {
		this.leitoId = leitoId;
		this.numeroQuarto = numeroQuarto;
		this.seqUnidadeFuncional = seqUnidadeFuncional;
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
	
}
