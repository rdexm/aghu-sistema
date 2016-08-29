package br.gov.mec.aghu.controleinfeccao.vo;

import br.gov.mec.aghu.core.commons.BaseBean;


public class ParamReportGrupoVO implements BaseBean {
	

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4696733889008831612L;
	
	private Integer pruSeq;
	private Short grrSeq;
	private Short edaSeq;
	private String nomeParamPermanente;
	private Short nroCopias;
	private Short nroCopiasDefault;
	private Short ordemEmissao;
	private Boolean indImpressao;
	private String edaDescricao;
	
	
	
	public ParamReportGrupoVO() {
		super();
	}
	
	public enum Fields {
		PRU_SEQ("pruSeq"),
		GRR_SEQ("grrSeq"),
		EDA_SEQ("edaSeq"),
		NOME_PARAM_PERMANENTE("nomeParamPermanente"),
		NRO_COPIAS("nroCopias"),
		NRO_COPIAS_DEFAULT("nroCopiasDefault"),
		ORDEM_EMISSAO("ordemEmissao"),
		IMPRESSAO("indImpressao"),
		EDA_DESCRICAO("edaDescricao");
	
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	public Integer getPruSeq() {
		return pruSeq;
	}

	public void setPruSeq(Integer pruSeq) {
		this.pruSeq = pruSeq;
	}

	public Short getGrrSeq() {
		return grrSeq;
	}

	public void setGrrSeq(Short grrSeq) {
		this.grrSeq = grrSeq;
	}

	public Short getEdaSeq() {
		return edaSeq;
	}

	public void setEdaSeq(Short edaSeq) {
		this.edaSeq = edaSeq;
	}

	public String getNomeParamPermanente() {
		return nomeParamPermanente;
	}

	public void setNomeParamPermanente(String nomeParamPermanente) {
		this.nomeParamPermanente = nomeParamPermanente;
	}

	public Short getNroCopias() {
		return nroCopias;
	}

	public void setNroCopias(Short nroCopias) {
		this.nroCopias = nroCopias;
	}

	public Short getNroCopiasDefault() {
		return nroCopiasDefault;
	}

	public void setNroCopiasDefault(Short nroCopiasDefault) {
		this.nroCopiasDefault = nroCopiasDefault;
	}

	public Short getOrdemEmissao() {
		return ordemEmissao;
	}

	public void setOrdemEmissao(Short ordemEmissao) {
		this.ordemEmissao = ordemEmissao;
	}

	public Boolean getIndImpressao() {
		return indImpressao;
	}

	public void setIndImpressao(Boolean indImpressao) {
		this.indImpressao = indImpressao;
	}

	public String getEdaDescricao() {
		return edaDescricao;
	}

	public void setEdaDescricao(String edaDescricao) {
		this.edaDescricao = edaDescricao;
	}
}