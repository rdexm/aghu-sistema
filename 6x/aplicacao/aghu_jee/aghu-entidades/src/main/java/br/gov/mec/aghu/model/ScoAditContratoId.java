package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;


import br.gov.mec.aghu.core.persistence.EntityCompositeId;


@Embeddable
public class ScoAditContratoId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7338621769296726344L;

	@Column(name = "SEQ", length= 7, nullable = false,precision = 3, scale = 0)
	private Integer seq;
	
	@Column(name = "CONT_SEQ", length= 7, nullable = false,precision = 3, scale = 0)
	private Integer contSeq;
	
	

	public ScoAditContratoId() {
	}


	public ScoAditContratoId(Integer seq, Integer contSeq) {
		super();
		this.seq = seq;
		this.contSeq = contSeq;
	}
	

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getContSeq() {
		return contSeq;
	}

	public void setContSeq(Integer contSeq) {
		this.contSeq = contSeq;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contSeq == null) ? 0 : contSeq.hashCode());
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
		ScoAditContratoId other = (ScoAditContratoId) obj;
		if (contSeq == null) {
			if (other.contSeq != null){
				return false;
			}
		} else if (!contSeq.equals(other.contSeq)){
			return false;
		}
		if (seq == null) {
			if (other.seq != null){
				return false;
			}
		} else if (!seq.equals(other.seq)){
			return false;
		}
		return true;
	}


	@Override
	public String toString() {
		return "ScoAditContratoId [seq=" + seq + ", contSeq=" + contSeq + "]";
	}
	
	
	
}
