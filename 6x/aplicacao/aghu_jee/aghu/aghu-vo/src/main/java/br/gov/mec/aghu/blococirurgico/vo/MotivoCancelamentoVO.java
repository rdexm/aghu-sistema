package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

public class MotivoCancelamentoVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5417146935184229105L;
	private Short seq;
	private String descricao;
	
	public MotivoCancelamentoVO(String descricao, Short seq) {
		this.descricao = descricao;
		this.seq = seq;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}	
		if (obj == null){
			return false;
		}	
		if (getClass() != obj.getClass()){
			return false;
		}	
		MotivoCancelamentoVO other = (MotivoCancelamentoVO) obj;
		if (seq == null) {
			if (other.seq != null){
				return false;
			}	
		} else if (!seq.equals(other.seq)){
			return false;
		}	
		return true;
	}

}
