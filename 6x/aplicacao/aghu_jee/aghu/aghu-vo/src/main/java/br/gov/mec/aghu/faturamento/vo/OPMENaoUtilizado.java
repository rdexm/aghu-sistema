package br.gov.mec.aghu.faturamento.vo;

public class OPMENaoUtilizado {
	private String descricao;

	public OPMENaoUtilizado(){}
	
	public OPMENaoUtilizado(String descricao) {
		this.descricao = descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public enum Fields {
		
		DESCRICAO("descricao");
		
		
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
