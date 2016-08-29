package br.gov.mec.aghu.prescricaomedica.vo;


public class ComponenteComposicaoVO implements java.io.Serializable {
	
	private static final long serialVersionUID = -4490933078030481842L;
	
	private Integer medMatCodigo;
	private Short ticSeq;
	private String seqUnidade;
	private Integer seqDosagem;
	private String descricaoUnidade;

	public ComponenteComposicaoVO() {
	}

	public Integer getMedMatCodigo() {
		return medMatCodigo;
	}

	public void setMedMatCodigo(Integer medMatCodigo) {
		this.medMatCodigo = medMatCodigo;
	}

	public Short getTicSeq() {
		return ticSeq;
	}

	public void setTicSeq(Short ticSeq) {
		this.ticSeq = ticSeq;
	}

	public String getSeqUnidade() {
		return seqUnidade;
	}

	public void setSeqUnidade(String seqUnidade) {
		this.seqUnidade = seqUnidade;
	}

	public Integer getSeqDosagem() {
		return seqDosagem;
	}

	public void setSeqDosagem(Integer seqDosagem) {
		this.seqDosagem = seqDosagem;
	}

	public String getDescricaoUnidade() {
		return descricaoUnidade;
	}

	public void setDescricaoUnidade(String descricaoUnidade) {
		this.descricaoUnidade = descricaoUnidade;
	}

	public enum Fields {
		MED_MAT_CODIGO("medMatCodigo"), 
		TIC_SEQ("ticSeq"), 
		SEQ_UNIDADE("seqUnidade"), 
		SEQ_DOSAGEM("seqDosagem"), 
		DESCRICAO_UNIDADE("descricaoUnidade");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

}
