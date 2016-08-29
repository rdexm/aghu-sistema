package br.gov.mec.aghu.controleinfeccao.vo;

import br.gov.mec.aghu.core.commons.BaseBean;


public class ProcedimentoCirurgicoVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3159827144479346444L;
	
	private Integer codigoProcedimento;
	private String descricaoProcedimento;
	private String descricaoGrupoProcedimento;
	
	public ProcedimentoCirurgicoVO() {
		super();
	}
	
	public String getDescricaoGrupoProcedimento() {
		return descricaoGrupoProcedimento;
	}

	public void setDescricaoGrupoProcedimento(String descricaoGrupoProcedimento) {
		this.descricaoGrupoProcedimento = descricaoGrupoProcedimento;
	}

	public String getDescricaoProcedimento() {
		return descricaoProcedimento;
	}

	public void setDescricaoProcedimento(String descricaoProcedimento) {
		this.descricaoProcedimento = descricaoProcedimento;
	}

	public Integer getCodigoProcedimento() {
		return codigoProcedimento;
	}

	public void setCodigoProcedimento(Integer codigoProcedimento) {
		this.codigoProcedimento = codigoProcedimento;
	}

	public enum Fields {

		CODIGO("codigoProcedimento"),
		DESCRICAO("descricaoProcedimento"),
		DESCRICAO_GRUPO("descricaoGrupoProcedimento");

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