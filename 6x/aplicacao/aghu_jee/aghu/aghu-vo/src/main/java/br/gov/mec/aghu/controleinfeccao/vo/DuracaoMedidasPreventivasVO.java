package br.gov.mec.aghu.controleinfeccao.vo;


import br.gov.mec.aghu.core.commons.BaseBean;

public class DuracaoMedidasPreventivasVO implements BaseBean {


	private static final long serialVersionUID = 8449010552707373999L;
	
	private Integer matricula;
	private Short vinCodigo;
	private Integer matriculaMovi;
	private Short vinCodigoMovi;
	
	
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
	
	public Integer getMatriculaMovi() {
		return matriculaMovi;
	}

	public void setMatriculaMovi(Integer matriculaMovi) {
		this.matriculaMovi = matriculaMovi;
	}

	public Short getVinCodigoMovi() {
		return vinCodigoMovi;
	}

	public void setVinCodigoMovi(Short vinCodigoMovi) {
		this.vinCodigoMovi = vinCodigoMovi;
	}


	public enum Fields {
		MATRICULA("matricula"), 
		CODIGO_VINCULO("vinCodigo"),
		MATRICULA_MOVI("matriculaMovi"), 
		CODIGO_VINCULO_MOVI("vinCodigoMovi")
		;


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
