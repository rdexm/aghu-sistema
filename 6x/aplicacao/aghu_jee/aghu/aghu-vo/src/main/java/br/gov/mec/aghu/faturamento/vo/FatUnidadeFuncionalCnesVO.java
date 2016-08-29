package br.gov.mec.aghu.faturamento.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class FatUnidadeFuncionalCnesVO implements BaseBean {

	private static final long serialVersionUID = 7284314714232430998L;
	
	private Short cnesSeq;
	private String servCodigo;
	private String claCodigo;
	private String claDescricao;
	private Integer claSeq;
	private Short cnesUnfSeq;
	
	
	public String getServCodigo() {
		return servCodigo;
	}
	public String getClaCodigo() {
		return claCodigo;
	}
	public String getClaDescricao() {
		return claDescricao;
	}
	public void setServCodigo(String servCodigo) {
		this.servCodigo = servCodigo;
	}
	public void setClaCodigo(String claCodigo) {
		this.claCodigo = claCodigo;
	}
	public void setClaDescricao(String claDescricao) {
		this.claDescricao = claDescricao;
	}
	
	public Short getCnesSeq() {
		return cnesSeq;
	}
	public void setCnesSeq(Short cnesSeq) {
		this.cnesSeq = cnesSeq;
	}

	public Integer getClaSeq() {
		return claSeq;
	}
	public void setClaSeq(Integer claSeq) {
		this.claSeq = claSeq;
	}

	public Short getCnesUnfSeq() {
		return cnesUnfSeq;
	}
	public void setCnesUnfSeq(Short cnesUnfSeq) {
		this.cnesUnfSeq = cnesUnfSeq;
	}

	public enum Fields {
		SERV_CODIGO("servCodigo"),
		CLA_CODIGO("claCodigo"),
		CLA_DESCRICAO("claDescricao"),
		CNES_SEQ("cnesSeq"), 
		CLA_SEQ("claSeq"), 
		CNES_UNF_SEQ("cnesUnfSeq")
		;

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
