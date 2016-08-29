package br.gov.mec.aghu.prescricaomedica.vo;

public class CursorIdadePacVO {

	private Integer anos;
	private Integer meses;
	private Double dias;
	private Integer soDias;
	
	public enum Fields {
		ANOS("anos"),
		MESES("meses"),
		DIAS("dias"),
		SO_DIAS("soDias");
		  
		private String field;
	
		private Fields(String field) {
			this.field = field;
		}
	
		@Override
		public String toString() {
			return this.field;
		}
	}

	public Integer getAnos() {
		return anos;
	}

	public void setAnos(Integer anos) {
		this.anos = anos;
	}

	public Integer getMeses() {
		return meses;
	}

	public void setMeses(Integer meses) {
		this.meses = meses;
	}

	public Double getDias() {
		return dias;
	}

	public void setDias(Double dias) {
		this.dias = dias;
	}

	public Integer getSoDias() {
		return soDias;
	}

	public void setSoDias(Integer soDias) {
		this.soDias = soDias;
	}
}
