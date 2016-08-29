package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;


public class VAacSiglaUnfSalaVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2404156287301010305L;
	private String sigla;
	private String descricao;
	private Short unfSeq;
	private Byte sala;
	
	private String setorSala = "";
	
	public VAacSiglaUnfSalaVO(Short unfSeq, String sigla, Byte sala) {
		this.unfSeq = unfSeq;
		this.sigla = sigla;
		this.sala = sala;
		this.setorSala = sigla + "/" + sala;
	}
	public VAacSiglaUnfSalaVO() {
	}
	
	public enum Fields {
		SETOR("sigla"),
		SALA("sala"),
		SEQ("unfSeq"),
		;
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
	

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

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Byte getSala() {
		return sala;
	}

	public void setSala(Byte sala) {
		this.sala = sala;
	}

	public String getSetorSala() {
		return setorSala;
	}

	public void setSetorSala(String setorSala) {
		this.setorSala = setorSala;
	}

}
