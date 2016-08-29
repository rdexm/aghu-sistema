package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Date;


public class ResultadoExamesVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3481960466410914799L;

	private Date dataHora;

	private Long valor;
	

	public ResultadoExamesVO() {
	}


	public Date getDataHora() {
		return dataHora;
	}


	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}


	public Long getValor() {
		return valor;
	}


	public void setValor(Long valor) {
		this.valor = valor;
	}

	public enum Fields {
		  VALOR("valor")
		, DTHR_LIBERADA("dataHora");
		  
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
