package br.gov.mec.aghu.prescricaomedica.vo;

public class TipoComposicaoComponenteVMpmDosagemVO implements java.io.Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3400017368029638392L;
	
	private Integer metMatCodigo;
	private String descricao;
	private Double qtde;
	private String seqUnidade;
	
	public TipoComposicaoComponenteVMpmDosagemVO() {}

	public enum Fields {
		MED_MAT_CODIGO("metMatCodigo"), 
		DESCRICAO("descricao"), 
		QTDE("qtde"), 
		SEQ_UNIDADE("seqUnidade");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	public Integer getMetMatCodigo() {
		return metMatCodigo;
	}

	public void setMetMatCodigo(Integer metMatCodigo) {
		this.metMatCodigo = metMatCodigo;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public Double getQtde() {
		return qtde;
	}
	
	public void setQtde(Double qtde) {
		this.qtde = qtde;
	}

	public String getSeqUnidade() {
		return seqUnidade;
	}

	public void setSeqUnidade(String seqUnidade) {
		this.seqUnidade = seqUnidade;
	}
}
