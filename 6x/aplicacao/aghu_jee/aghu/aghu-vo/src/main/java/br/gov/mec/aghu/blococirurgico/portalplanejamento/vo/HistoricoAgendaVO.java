package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;


public class HistoricoAgendaVO {

	private Integer seqp;
	private String data;
	private String origem;
	private String operacao;
	private String informadoPor;
	private String ocorrencia;
	
	public HistoricoAgendaVO(String data, String origem, String operacao, String informadoPor, String ocorrencia){
		this.data = data;
		this.origem = origem;
		this.operacao = operacao;
		this.informadoPor = informadoPor;
		this.ocorrencia = ocorrencia;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public String getOperacao() {
		return operacao;
	}

	public void setOperacao(String operacao) {
		this.operacao = operacao;
	}

	public String getInformadoPor() {
		return informadoPor;
	}

	public void setInformadoPor(String informadoPor) {
		this.informadoPor = informadoPor;
	}

	public String getOcorrencia() {
		return ocorrencia;
	}

	public void setOcorrencia(String ocorrencia) {
		this.ocorrencia = ocorrencia;
	}

	public Integer getSeqp() {
		return seqp;
	}

	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}
	
}
