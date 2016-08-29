package br.gov.mec.aghu.farmacia.vo;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class TipoUsoMedicamentoVO implements BaseBean{

	private static final long serialVersionUID = -6182348725219607744L;
	private String sigla;
	private String descricao;
	private DominioSituacao indSituacao;

	//Métodos construtores
	public TipoUsoMedicamentoVO() {

	}

	public TipoUsoMedicamentoVO(String sigla, String descricao) {
		super();
		this.sigla = sigla;
		this.descricao = descricao;
	}

	//Getters & Setters
	public String getSigla() {
		return sigla;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}
	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	/**
	 * FIELDS
	 */
	public enum Fields {
		SIGLA("sigla"),
		DESCRICAO("descricao"),
		IND_SITUACAO("indSituacao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}


}
