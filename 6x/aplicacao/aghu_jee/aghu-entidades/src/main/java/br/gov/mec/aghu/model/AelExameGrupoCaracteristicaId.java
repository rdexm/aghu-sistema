package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the ael_exame_grupo_caracts database table.
 * 
 */
@Embeddable
public class AelExameGrupoCaracteristicaId implements EntityCompositeId {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2622737712273289069L;
	private String emaExaSigla;
	private Integer emaManSeq;
	private Integer cacSeq;
	private Integer gcaSeq;

    public AelExameGrupoCaracteristicaId() {
    }

	@Column(name="EMA_EXA_SIGLA")
	public String getEmaExaSigla() {
		return this.emaExaSigla;
	}
	public void setEmaExaSigla(String emaExaSigla) {
		this.emaExaSigla = emaExaSigla;
	}

	@Column(name="EMA_MAN_SEQ")
	public Integer getEmaManSeq() {
		return this.emaManSeq;
	}
	public void setEmaManSeq(Integer emaManSeq) {
		this.emaManSeq = emaManSeq;
	}

	@Column(name="CAC_SEQ")
	public Integer getCacSeq() {
		return this.cacSeq;
	}
	public void setCacSeq(Integer cacSeq) {
		this.cacSeq = cacSeq;
	}

	@Column(name="GCA_SEQ")
	public Integer getGcaSeq() {
		return this.gcaSeq;
	}
	public void setGcaSeq(Integer gcaSeq) {
		this.gcaSeq = gcaSeq;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AelExameGrupoCaracteristicaId)) {
			return false;
		}
		AelExameGrupoCaracteristicaId castOther = (AelExameGrupoCaracteristicaId)other;
		return 
			this.emaExaSigla.equals(castOther.emaExaSigla)
			&& this.emaManSeq.equals(castOther.emaManSeq)
			&& this.cacSeq.equals(castOther.cacSeq)
			&& this.gcaSeq.equals(castOther.gcaSeq);

    }
    
	public int hashCode() {
		final Integer prime = 31;
		Integer hash = 17;
		hash = hash * prime + this.emaExaSigla.hashCode();
		hash = hash * prime + this.emaManSeq.hashCode();
		hash = hash * prime + this.cacSeq.hashCode();
		hash = hash * prime + this.gcaSeq.hashCode();
		
		return hash;
    }
}