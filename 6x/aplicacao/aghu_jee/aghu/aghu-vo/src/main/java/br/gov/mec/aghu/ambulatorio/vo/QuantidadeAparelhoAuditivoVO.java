package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

public class QuantidadeAparelhoAuditivoVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5541684L;

	private Integer atdSeq;
	private Long quantidade;
	
	public enum Fields {
		ATD_SEQ("atdSeq"),
		QUANTIDADE("quantidade");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
	
	public Integer getAtdSeq() {
		return atdSeq;
	}
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
	public Long getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Long quantidade) {
		this.quantidade = quantidade;
	}
	
}
