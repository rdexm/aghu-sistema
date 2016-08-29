package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;


public class SubRelatorioNotasDeConsumoDaSalaSangueUtilizadoVO implements Serializable {
	
	private static final long serialVersionUID = -2047178099786822060L;

	private String sangueUtilizado;  //75
	
	public SubRelatorioNotasDeConsumoDaSalaSangueUtilizadoVO() {
		super();
	}
	
	public SubRelatorioNotasDeConsumoDaSalaSangueUtilizadoVO(String sangueUtilizado) {
		super();
		this.sangueUtilizado = sangueUtilizado;
	}

	public enum Fields {

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

	public String getSangueUtilizado() {
		return sangueUtilizado;
	}

	public void setSangueUtilizado(String sangueUtilizado) {
		this.sangueUtilizado = sangueUtilizado;
	}
}