package br.gov.mec.aghu.patrimonio.vo;

import java.io.Serializable;

public class CepVO implements Serializable {

	private static final long serialVersionUID = -7495635567578826846L;
	private Integer lgrCodigo;
	private Integer cep;
	
	public enum Fields {
		
		LGR_CODIGO("lgrCodigo"),
		CEP("cep");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Integer getLgrCodigo() {
		return lgrCodigo;
	}

	public void setLgrCodigo(Integer lgrCodigo) {
		this.lgrCodigo = lgrCodigo;
	}

	public Integer getCep() {
		return cep;
	}

	public void setCep(Integer cep) {
		this.cep = cep;
	}
	
}
