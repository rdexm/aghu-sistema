package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class VAelExamesLiberadosId implements EntityCompositeId {

	
	private static final long serialVersionUID = 328115631045447938L;
	
	private Integer soeSeq;
	private Short seqP;

	
	@Column(name = "SEQP", nullable = false, precision = 3, scale = 0)
	public Short getSeqP() {
		return seqP;
	}
	
	public void setSeqP(Short seqP) {
		this.seqP = seqP;
	}
	
	@Column(name = "SOE_SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getSoeSeq() {
		return soeSeq;
	}
	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seqP == null) ? 0 : seqP.hashCode());
		result = prime * result + ((soeSeq == null) ? 0 : soeSeq.hashCode());
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
		VAelExamesLiberadosId other = (VAelExamesLiberadosId) obj;
		if (seqP == null) {
			if (other.seqP != null){
				return false;
			}
		} else if (!seqP.equals(other.seqP)){
			return false;
		}
		if (soeSeq == null) {
			if (other.soeSeq != null){
				return false;
			}
		} else if (!soeSeq.equals(other.soeSeq)){
			return false;
		}
		return true;
	}
	
	


	
}
