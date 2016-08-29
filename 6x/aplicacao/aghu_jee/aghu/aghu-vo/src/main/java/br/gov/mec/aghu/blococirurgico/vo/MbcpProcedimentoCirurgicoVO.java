package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


public class MbcpProcedimentoCirurgicoVO extends BaseEntitySeq<Integer> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 739341601264198375L;
	
	private Integer seq;
	private String descricao;
	
	public MbcpProcedimentoCirurgicoVO() {}

	public MbcpProcedimentoCirurgicoVO(Integer seq, String descricao) {
		super();
		this.seq = seq;
		this.descricao = descricao;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public enum Fields {

		SEQ("seq"), DESCRICAO("descricao");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MbcpProcedimentoCirurgicoVO)) {
			return false;
		}
		MbcpProcedimentoCirurgicoVO other = (MbcpProcedimentoCirurgicoVO) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}

}