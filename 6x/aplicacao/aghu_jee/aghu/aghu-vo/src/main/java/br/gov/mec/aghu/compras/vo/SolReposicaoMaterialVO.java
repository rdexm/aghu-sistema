package br.gov.mec.aghu.compras.vo;



public class SolReposicaoMaterialVO {

	private Integer slcNumero;
	private Long qtdAprovada;
	private String descricaoLote;
	private Integer ltrSeq;
	private Integer seqItem;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((slcNumero == null) ? 0 : slcNumero.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		SolReposicaoMaterialVO other = (SolReposicaoMaterialVO) obj;
		if (slcNumero == null) {
			if (other.slcNumero != null){
				return false;
			}
		} else if (!slcNumero.equals(other.slcNumero)){
			return false;
		}
		return true;
	}
	
	public Integer getSlcNumero() {
		return slcNumero;
	}

	public void setSlcNumero(Integer slcNumero) {
		this.slcNumero = slcNumero;
	}

	public Long getQtdAprovada() {
		return qtdAprovada;
	}

	public void setQtdAprovada(Long qtdAprovada) {
		this.qtdAprovada = qtdAprovada;
	}

	public String getDescricaoLote() {
		return descricaoLote;
	}

	public void setDescricaoLote(String descricaoLote) {
		this.descricaoLote = descricaoLote;
	}

	public Integer getLtrSeq() {
		return ltrSeq;
	}

	public void setLtrSeq(Integer ltrSeq) {
		this.ltrSeq = ltrSeq;
	}

	public Integer getSeqItem() {
		return seqItem;
	}

	public void setSeqItem(Integer seqItem) {
		this.seqItem = seqItem;
	}

	public enum Fields {
		
		SLC_NUMERO("slcNumero"),
		LTR_SEQ("ltrSeq"),
		SEQ_ITEM("seqItem"),
		QTD_APROVADA("qtdAprovada"),
		DESCRICAO_LOTE("descricaoLote");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
}
