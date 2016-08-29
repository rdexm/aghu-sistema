package br.gov.mec.aghu.compras.vo;

public class RamoComercialVO {
	
	private Short codigo;
	private String descricao;
	
	public enum Fields{
		
		CODIGO("codigo"),
		DESCRICAO("descricao");
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public RamoComercialVO() {
		super();
	}

	public Short getCodigo() {
		return codigo;
	}
	
	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}	
	
}
