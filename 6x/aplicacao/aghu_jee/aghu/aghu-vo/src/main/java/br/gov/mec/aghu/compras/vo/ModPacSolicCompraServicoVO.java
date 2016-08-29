package br.gov.mec.aghu.compras.vo;

public class ModPacSolicCompraServicoVO {

	private String descricaoModalidade;
	private String codigoModalidade;
	private String descricaoObjeto;
	private String tempoPrevisto;
	private String localidadeAtual;

	public String getDescricaoModalidade() {
		return descricaoModalidade;
	}

	public void setDescricaoModalidade(String descricaoModalidade) {
		this.descricaoModalidade = descricaoModalidade;
	}

	public String getCodigoModalidade() {
		return codigoModalidade;
	}

	public void setCodigoModalidade(String codigoModalidade) {
		this.codigoModalidade = codigoModalidade;
	}

	public String getDescricaoObjeto() {
		return descricaoObjeto;
	}

	public void setDescricaoObjeto(String descricaoObjeto) {
		this.descricaoObjeto = descricaoObjeto;
	}

	public String getTempoPrevisto() {
		return tempoPrevisto;
	}

	public void setTempoPrevisto(String tempoPrevisto) {
		this.tempoPrevisto = tempoPrevisto;
	}

	public String getLocalidadeAtual() {
		return localidadeAtual;
	}

	public void setLocalidadeAtual(String localidadeAtual) {
		this.localidadeAtual = localidadeAtual;
	}

	public enum Fields {

		DESCRICAO_MODALIDADE("descricaoModalidade"), 
		DESCRICAO_OBJETO("descricaoObjeto"),
		TEMPO_PREVISTO("tempoPrevisto"),
		LOCALIDADE_ATUAL("localidadeAtual");

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
