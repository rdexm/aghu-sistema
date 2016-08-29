package br.gov.mec.aghu.compras.vo;


public class AutorizacaoFornVO {

	/*** Filtro ***/
	private Integer lctNumero;

	public AutorizacaoFornVO() {
		super();
	}
	
	public AutorizacaoFornVO(Integer lctNumero) {
		this.lctNumero = lctNumero;
	}
	
	public Integer getLctNumero() {
		return lctNumero;
	}

	public void setLctNumero(Integer lctNumero) {
		this.lctNumero = lctNumero;
	}
	
	public enum Fields {
		PFR_LCT_NUMERO("lctNumero");
		
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
}
