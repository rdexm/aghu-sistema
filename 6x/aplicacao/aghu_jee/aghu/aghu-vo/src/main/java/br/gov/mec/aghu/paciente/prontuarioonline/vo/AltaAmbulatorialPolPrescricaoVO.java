package br.gov.mec.aghu.paciente.prontuarioonline.vo;


public class AltaAmbulatorialPolPrescricaoVO {

	private Long seq;
	private String descricao;
	
	public enum Fields {
		
		SEQ("seq"),
		DESCRICAO("descricao");		

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

		
	// GETTERS and SETTERS

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	

}
