package br.gov.mec.aghu.faturamento.vo;

public class FatTabFinanciamentoVO {

	private Integer seq;
	
	private String codigo;
	
	private String descricao;
	
	private Integer seqSus;

	private TipoProcessado processado;
	
	public FatTabFinanciamentoVO() {}
	
	public FatTabFinanciamentoVO(Integer seq, String codigo, String descricao, Integer seqSus,
			TipoProcessado processado) {
		super();
		this.seq = seq;
		this.codigo = codigo;
		this.descricao = descricao;
		this.seqSus = seqSus;
		this.processado = processado;
	}

	public enum TipoProcessado {

		INCLUI("I"),
		ALTERA("A"),
		PROCESSADO("S"),
		NAO_PROCESSADO("N"),
		DESPREZADO("D");
		
		private String fields;

		private TipoProcessado(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getSeqSus() {
		return seqSus;
	}

	public void setSeqSus(Integer seqSus) {
		this.seqSus = seqSus;
	}

	public TipoProcessado getProcessado() {
		return processado;
	}

	public void setProcessado(TipoProcessado processado) {
		this.processado = processado;
	}
	
	
}