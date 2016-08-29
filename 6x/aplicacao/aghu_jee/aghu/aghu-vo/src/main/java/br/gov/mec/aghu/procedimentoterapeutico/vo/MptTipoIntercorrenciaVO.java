package br.gov.mec.aghu.procedimentoterapeutico.vo;


public class MptTipoIntercorrenciaVO  implements java.io.Serializable {	

	private static final long serialVersionUID = 4414830029359917408L;

	private String descricao;
	private Short seq;
	
	public enum Fields {
		
		DESCRICAO("descricao"),
		SEQ("seq");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

}