package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;

public class FatMotivoRejeicaoContasVO implements Serializable {

	private static final long serialVersionUID = 2602040038704147887L;

	private String descricao;

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public enum Fields {

		DESCRICAO("descricao");
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
	
}
