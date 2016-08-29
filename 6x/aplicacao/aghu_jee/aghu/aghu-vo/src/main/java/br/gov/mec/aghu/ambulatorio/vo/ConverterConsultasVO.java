package br.gov.mec.aghu.ambulatorio.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class ConverterConsultasVO implements BaseBean {

	private static final long serialVersionUID = -4961167409092621660L;
	private static final String HIFEN = "-";

	private Short pgdSeq;
	private Short tagSeq;
	private Short caaSeq;
	private String pagador;
	private String tipo;
	private String condicao;
	
	public enum Fields {
		PGD_SEQ	("pgdSeq"),
		TAG_SEQ	("tagSeq"),
		CAA_SEQ	("caaSeq"),
		PAGADOR("pagador"),
		TIPO("tipo"),
		CONDICAO("condicao");
		

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public Short getPgdSeq() {
		return pgdSeq;
	}
	public void setPgdSeq(Short pgdSeq) {
		this.pgdSeq = pgdSeq;
	}
	public Short getTagSeq() {
		return tagSeq;
	}
	public void setTagSeq(Short tagSeq) {
		this.tagSeq = tagSeq;
	}
	public Short getCaaSeq() {
		return caaSeq;
	}
	public void setCaaSeq(Short caaSeq) {
		this.caaSeq = caaSeq;
	}
	public String getPagador() {
		return pagador;
	}
	public void setPagador(String pagador) {
		this.pagador = pagador;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getCondicao() {
		return condicao;
	}
	public void setCondicao(String condicao) {
		this.condicao = condicao;
	}
	
	public String getSeqPagadorTipoCondicao() {
		return this.getPgdSeq().toString() + HIFEN + this.getTagSeq().toString() + HIFEN + this.getCaaSeq().toString();
	}

	public String getPagadorTipoCondicao() {
		return this.getPagador() + HIFEN + this.getTipo() + HIFEN + this.getCondicao();
	}
	
	public String getTipoCondicao() {
		return this.getTipo() + HIFEN + this.getCondicao();
	}

}
