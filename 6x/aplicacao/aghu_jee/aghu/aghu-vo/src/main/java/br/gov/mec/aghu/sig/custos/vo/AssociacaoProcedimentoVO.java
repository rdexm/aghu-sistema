package br.gov.mec.aghu.sig.custos.vo;



public class AssociacaoProcedimentoVO implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1585544326425322979L;
	
	private Integer phiSeq;
	private String phiDescricao;
	private Long codTabela;
	
	public AssociacaoProcedimentoVO() {
		super();
	}

	
	public enum Fields {
		PHI_SEQ("phiSeq"),
		PHI_DESCRICAO("phiDescricao"),
		COD_TABELA("codTabela");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}


	public Integer getPhiSeq() {
		return phiSeq;
	}


	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}


	public String getPhiDescricao() {
		return phiDescricao;
	}


	public void setPhiDescricao(String phiDescricao) {
		this.phiDescricao = phiDescricao;
	}


	public Long getCodTabela() {
		return codTabela;
	}


	public void setCodTabela(Long codTabela) {
		this.codTabela = codTabela;
	}

}
