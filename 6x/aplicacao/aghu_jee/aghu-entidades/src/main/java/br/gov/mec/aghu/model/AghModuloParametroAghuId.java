package br.gov.mec.aghu.model;

import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class AghModuloParametroAghuId implements EntityCompositeId {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3695011292233976142L;
	private Integer seqParametro;
	private Integer seqModuloAghu;
	
	public Integer getSeqParametro() {
		return seqParametro;
	}
	
	public void setSeqParametro(Integer seqParametro) {
		this.seqParametro = seqParametro;
	}
	
	public Integer getSeqModuloAghu() {
		return seqModuloAghu;
	}
	
	public void setSeqModuloAghu(Integer seqModuloAghu) {
		this.seqModuloAghu = seqModuloAghu;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((seqModuloAghu == null) ? 0 : seqModuloAghu.hashCode());
		result = prime * result
				+ ((seqParametro == null) ? 0 : seqParametro.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		AghModuloParametroAghuId other = (AghModuloParametroAghuId) obj;
		if (seqModuloAghu == null) {
			if (other.seqModuloAghu != null) {
				return false;
			}
		} else if (!seqModuloAghu.equals(other.seqModuloAghu)) {
			return false;
		}
		if (seqParametro == null) {
			if (other.seqParametro != null) {
				return false;
			}
		} else if (!seqParametro.equals(other.seqParametro)) {
			return false;
		}
		return true;
	}
	
}