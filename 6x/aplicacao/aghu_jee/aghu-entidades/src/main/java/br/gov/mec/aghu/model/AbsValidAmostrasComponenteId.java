package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the abs_valid_amostras_componentes database table.
 * 
 */
@Embeddable
public class AbsValidAmostrasComponenteId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8846257298253555596L;

	@Column(name="CSA_CODIGO")
	private String csaCodigo;

	@Column(name="SEQP")
	private Integer seqp;

    public AbsValidAmostrasComponenteId() {
    }
	public String getCsaCodigo() {
		return this.csaCodigo;
	}
	public void setCsaCodigo(String csaCodigo) {
		this.csaCodigo = csaCodigo;
	}
	public Integer getSeqp() {
		return this.seqp;
	}
	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AbsValidAmostrasComponenteId)) {
			return false;
		}
		AbsValidAmostrasComponenteId castOther = (AbsValidAmostrasComponenteId)other;
		return 
			this.csaCodigo.equals(castOther.csaCodigo)
			&& this.seqp.equals(castOther.seqp);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.csaCodigo.hashCode();
		hash = hash * prime + this.seqp.hashCode();
		
		return hash;
    }
	
	
}