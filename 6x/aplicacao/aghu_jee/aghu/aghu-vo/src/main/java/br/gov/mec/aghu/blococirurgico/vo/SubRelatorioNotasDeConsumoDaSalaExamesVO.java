package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;


public class SubRelatorioNotasDeConsumoDaSalaExamesVO implements Serializable {
	
	private static final long serialVersionUID = -2047178099786822060L;

	private String descricaoExame;   //74
	private String sangueUtilizado;  //75
	
	public SubRelatorioNotasDeConsumoDaSalaExamesVO() {
		super();
	}
	
	public SubRelatorioNotasDeConsumoDaSalaExamesVO(String descricaoExame) {
		super();
		this.descricaoExame = descricaoExame;
	}

	public enum Fields {

		DESCRICAO_EXAME("descricaoExame"),
		SANGUE_UTILIZADO("sangueUtilizado");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
	
	//Getters and Setters
	
	public String getDescricaoExame() {
		return descricaoExame;
	}

	public void setDescricaoExame(String descricaoExame) {
		this.descricaoExame = descricaoExame;
	}

	public String getSangueUtilizado() {
		return sangueUtilizado;
	}

	public void setSangueUtilizado(String sangueUtilizado) {
		this.sangueUtilizado = sangueUtilizado;
	}
}