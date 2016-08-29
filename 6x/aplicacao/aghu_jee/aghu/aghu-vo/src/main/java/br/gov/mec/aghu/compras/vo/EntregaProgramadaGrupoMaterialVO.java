package br.gov.mec.aghu.compras.vo;


public class EntregaProgramadaGrupoMaterialVO extends ProgramacaoEntregaGlobalVO {

	private Integer gmtCodigo;
	private String gmtDescricao;

	public enum Fields {
		GMT_CODIGO("gmtCodigo"), GMT_DESCRICAO("gmtDescricao"), 
		SALDO_PROGRAMADO("saldoProgramado"), VALOR_LIBERAR("valorALiberar"), 
		VALOR_LIBERADO("valorLiberado"), VALOR_ATRASO("valorEmAtraso");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public Integer getGmtCodigo() {
		return gmtCodigo;
	}

	public void setGmtCodigo(Integer gmtCodigo) {
		this.gmtCodigo = gmtCodigo;
	}

	public String getGmtDescricao() {
		return gmtDescricao;
	}

	public void setGmtDescricao(String gmtDescricao) {
		this.gmtDescricao = gmtDescricao;
	}

}