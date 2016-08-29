package br.gov.mec.aghu.estoque.vo;

public class MaterialPericulosoVO {
	
	private Boolean indCorrosivo;
	private Boolean indInflamavel;
	private Boolean indTermolabil;
	private Boolean indRadioativo;
	private Boolean indToxico;
	
	
	public Boolean getIndCorrosivo() {
		return indCorrosivo;
	}
	public void setIndCorrosivo(Boolean indCorrosivo) {
		this.indCorrosivo = indCorrosivo;
	}
	public Boolean getIndInflamavel() {
		return indInflamavel;
	}
	public void setIndInflamavel(Boolean indInflamavel) {
		this.indInflamavel = indInflamavel;
	}
	public Boolean getIndTermolabil() {
		return indTermolabil;
	}
	public void setIndTermolabil(Boolean indTermolabil) {
		this.indTermolabil = indTermolabil;
	}
	public Boolean getIndRadioativo() {
		return indRadioativo;
	}
	public void setIndRadioativo(Boolean indRadioativo) {
		this.indRadioativo = indRadioativo;
	}
	public Boolean getIndToxico() {
		return indToxico;
	}
	public void setIndToxico(Boolean indToxico) {
		this.indToxico = indToxico;
	}
	
	public enum Fields {
		
		IND_CORROSIVO("indCorrosivo"),
		IND_INFLAMAVEL("indInflamavel"),
		IND_RADIOATIVO("indRadioativo"),
		IND_TOXICO("indToxico"),
		IND_TERMOLABIL("indTermolabil");
	
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
