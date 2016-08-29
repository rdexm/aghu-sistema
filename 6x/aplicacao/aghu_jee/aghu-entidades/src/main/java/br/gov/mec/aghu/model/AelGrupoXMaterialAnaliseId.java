package br.gov.mec.aghu.model;

import javax.persistence.Column;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

public class AelGrupoXMaterialAnaliseId implements EntityCompositeId {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4130538436795846226L;
	private Integer gmaSeq;
	private Integer manSeq;

	public AelGrupoXMaterialAnaliseId() {
	}

	@Column(name="GMA_SEQ")
	public Integer getGmaSeq() {
		return gmaSeq;
	}

	public void setGmaSeq(Integer gmaSeq) {
		this.gmaSeq = gmaSeq;
	}

	@Column(name="MAN_SEQ")
	public Integer getManSeq() {
		return manSeq;
	}

	public void setManSeq(Integer manSeq) {
		this.manSeq = manSeq;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gmaSeq == null) ? 0 : gmaSeq.hashCode());
		result = prime * result + ((manSeq == null) ? 0 : manSeq.hashCode());
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
		if (!(obj instanceof AelGrupoXMaterialAnaliseId)) {
			return false;
		}
		AelGrupoXMaterialAnaliseId other = (AelGrupoXMaterialAnaliseId) obj;
		if (gmaSeq == null) {
			if (other.gmaSeq != null) {
				return false;
			}
		} else if (!gmaSeq.equals(other.gmaSeq)) {
			return false;
		}
		if (manSeq == null) {
			if (other.manSeq != null) {
				return false;
			}
		} else if (!manSeq.equals(other.manSeq)) {
			return false;
		}
		return true;
	}
	
	
}