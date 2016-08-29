package br.gov.mec.aghu.controleinfeccao.vo;

import br.gov.mec.aghu.core.commons.BaseBean;


public class ParamReportUsuarioVO implements BaseBean {
	

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4696733889008831612L;
	
	private Integer seq;
	private String nomeParamPermanente;
	private Short nroCopias;
	
	
	
	
	public ParamReportUsuarioVO() {
		super();
	}
	
	public enum Fields {
		SEQ("seq"),
		NOME_PARAM_PERMANENTE("nomeParamPermanente"),
		NRO_COPIAS("nroCopias");
	
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
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
}