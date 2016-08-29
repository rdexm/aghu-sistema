package br.gov.mec.aghu.farmacia.vo;

import java.io.Serializable;

public class UnidadeFuncionalVO implements Serializable {

	private static final long serialVersionUID = -8677834777258670470L;

	private Short seq;
	private Byte andar;
	private String ala;
	private String unfDescricao;
	private String andarAlaDescricao;
	
	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public Byte getAndar() {
		return andar;
	}

	public void setAndar(Byte andar) {
		this.andar = andar;
	}

	public String getAla() {
		return ala;
	}

	public void setAla(String ala) {
		this.ala = ala;
	}

	public String getUnfDescricao() {
		return unfDescricao;
	}

	public void setUnfDescricao(String unfDescricao) {
		this.unfDescricao = unfDescricao;
	}

	public String getAndarAlaDescricao() {
		return andarAlaDescricao;
	}

	public void setAndarAlaDescricao(String andarAlaDescricao) {
		this.andarAlaDescricao = andarAlaDescricao;
	}

	/**
	 * FIELDS
	 * 
	 */
	public enum Fields {
		SEQ("seq"),
		ANDAR("andar"),
		ALA("ala"),
		UNF_DESCRICAO("unfDescricao"),
		ANDAR_ALA_DESCRICAO("andarAlaDescricao"),
		;

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
