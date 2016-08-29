package br.gov.mec.aghu.sig.custos.vo;


public class ProcessamentoCustoFinalizadoVO implements java.io.Serializable {

	private static final long serialVersionUID = -5485160171797479633L;
	private Integer seqProcessamentoCusto;
	private String competencia;
	private boolean ocorreuErroProcessamento;
	
	public ProcessamentoCustoFinalizadoVO(Integer seqProcessamentoCusto,
			String competencia, boolean ocorreuErroProcessamento) {
		super();
		this.seqProcessamentoCusto = seqProcessamentoCusto;
		this.competencia = competencia;
		this.ocorreuErroProcessamento = ocorreuErroProcessamento;
	}
	public Integer getSeqProcessamentoCusto() {
		return seqProcessamentoCusto;
	}
	public void setSeqProcessamentoCusto(Integer seqProcessamentoCusto) {
		this.seqProcessamentoCusto = seqProcessamentoCusto;
	}
	public String getCompetencia() {
		return competencia;
	}
	public void setCompetencia(String competencia) {
		this.competencia = competencia;
	}
	public boolean isOcorreuErroProcessamento() {
		return ocorreuErroProcessamento;
	}
	public void setOcorreuErroProcessamento(boolean ocorreuErroProcessamento) {
		this.ocorreuErroProcessamento = ocorreuErroProcessamento;
	}
}
