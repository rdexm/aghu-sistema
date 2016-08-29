package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

public class CurEspecialidadeVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2615739463019165468L;
	
	private Integer clcCodigo;
	private Boolean indEspPediatrica;	
	
	public enum Fields {
		CLC_CODIGO("clcCodigo"),
		IND_ESP_PEDIATRICA("indEspPediatrica");
		
		private String field;
		
		private Fields(String field) {
			this.field = field;
		}
		
		@Override
		public String toString() {
			return this.field;
		}
	}

	public Integer getClcCodigo() {
		return clcCodigo;
	}

	public void setClcCodigo(Integer clcCodigo) {
		this.clcCodigo = clcCodigo;
	}

	public Boolean getIndEspPediatrica() {
		return indEspPediatrica;
	}

	public void setIndEspPediatrica(Boolean indEspPediatrica) {
		this.indEspPediatrica = indEspPediatrica;
	}
}