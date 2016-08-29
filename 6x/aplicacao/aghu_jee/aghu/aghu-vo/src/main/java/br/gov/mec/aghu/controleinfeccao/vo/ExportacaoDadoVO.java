package br.gov.mec.aghu.controleinfeccao.vo;

import br.gov.mec.aghu.core.commons.BaseBean;


public class ExportacaoDadoVO implements BaseBean {
	

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4696733889008831612L;
	
	private Short seq;
	private String descricao;
	
	public ExportacaoDadoVO() {
		super();
	}
	
	public enum Fields {
		SEQ("seq"),
		DESCRICAO("descricao");
	
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}