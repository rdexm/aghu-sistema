package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the ael_resultados_codificados database table.
 * 
 */
@Embeddable
public class AelEspecialidadeCampoLaudoId implements EntityCompositeId {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8655055078284049482L;
	private Integer calSeq; 
	private Short espSeq;

	public AelEspecialidadeCampoLaudoId() {
    }
	
	public AelEspecialidadeCampoLaudoId(Integer calSeq, Short espSeq) {
		super();
		this.calSeq = calSeq;
		this.espSeq = espSeq;
	}

	@Column(name="CAL_SEQ")
	public Integer getCalSeq() {
		return calSeq;
	}

	public void setCalSeq(Integer calSeq) {
		this.calSeq = calSeq;
	}

	@Column(name="ESP_SEQ")
	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((calSeq == null) ? 0 : calSeq.hashCode());
		result = prime * result + ((espSeq == null) ? 0 : espSeq.hashCode());
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
		if (!(obj instanceof AelEspecialidadeCampoLaudoId)) {
			return false;
		}
		AelEspecialidadeCampoLaudoId other = (AelEspecialidadeCampoLaudoId) obj;
		if (calSeq == null) {
			if (other.calSeq != null) {
				return false;
			}
		} else if (!calSeq.equals(other.calSeq)) {
			return false;
		}
		if (espSeq == null) {
			if (other.espSeq != null) {
				return false;
			}
		} else if (!espSeq.equals(other.espSeq)) {
			return false;
		}
		return true;
	}
	
	
}