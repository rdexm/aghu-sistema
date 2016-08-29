package br.gov.mec.aghu.faturamento.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class FatCnesVO implements BaseBean {

	private static final long serialVersionUID = 8137403592223594537L;

	private Integer codigoClassificacao;
	private String servico;
	private String classificacao;
	private String descricao;
	private Short codServico;

	public enum Fields {
		CODIGO_CLASSIFICACAO("codigoClassificacao"), SERVICO("servico"), CLASSIFICACAO("classificacao"), DESCRICAO(
				"descricao"), COD_SERVICO("codServico");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public Integer getCodigoClassificacao() {
		return codigoClassificacao;
	}

	public String getServico() {
		return servico;
	}

	public String getClassificacao() {
		return classificacao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setCodigoClassificacao(Integer codigoClassificacao) {
		this.codigoClassificacao = codigoClassificacao;
	}

	public void setServico(String servico) {
		this.servico = servico;
	}

	public void setClassificacao(String classificacao) {
		this.classificacao = classificacao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Short getCodServico() {
		return codServico;
	}

	public void setCodServico(Short codServico) {
		this.codServico = codServico;
	}

}
