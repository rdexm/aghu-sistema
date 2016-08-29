package br.gov.mec.aghu.transplante.vo;

import java.io.Serializable;
import java.util.Date;

public class TiposExamesPacienteVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7584962214536L;

	private String descricaoUsual;
	private String resultado;
	private Date dthrLiberada;
	
	public enum Fields {
		DESCRICAO_USUAL("descricaoUsual"),
		DTHR_LIBERADA("dthrLiberada"),
		RESULTADO("resultado");
		  
		private String field;
	
		private Fields(String field) {
			this.field = field;
		}
	
		@Override
		public String toString() {
			return this.field;
		}
	}
	
	public String getDescricaoUsual() {
		return descricaoUsual;
	}
	public void setDescricaoUsual(String descricaoUsual) {
		this.descricaoUsual = descricaoUsual;
	}
	public Date getDthrLiberada() {
		return dthrLiberada;
	}
	public void setDthrLiberada(Date dthrLiberada) {
		this.dthrLiberada = dthrLiberada;
	}
	public String getResultado() {
		return resultado;
	}
	public void setResultado(String resultado) {
		this.resultado = resultado;
	}
}
