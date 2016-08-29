package br.gov.mec.aghu.controleinfeccao.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class GrupoProcedRiscoVO implements BaseBean {


	private static final long serialVersionUID = 8449010552707373999L;
	
	private Integer matricula;
	private Short vinCodigo;
	
	
	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public enum Fields {
		MATRICULA("matricula"), 
		CODIGO_VINCULO("vinCodigo");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

}
