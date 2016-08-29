package br.gov.mec.aghu.transplante.vo;

import java.util.Date;

public class ResultadoExameCulturalVO {

	private String descricao;
	private Date dthrLiberada;
	
	public enum Fields {
		
		DATA_LIBERADA("dthrLiberada"),
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

	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Date getDthrLiberada() {
		return dthrLiberada;
	}
	public void setDthrLiberada(Date dthrLiberada) {
		this.dthrLiberada = dthrLiberada;
	}

}


