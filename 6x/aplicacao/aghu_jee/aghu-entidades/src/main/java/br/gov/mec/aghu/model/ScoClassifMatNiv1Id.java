package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the sco_classif_mat_niv1 database table.
 * 
 */
@Embeddable
public class ScoClassifMatNiv1Id implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6402090336896795501L;
	private Integer gmtCodigo;
	private Integer codigo;

    public ScoClassifMatNiv1Id() {
    }

	@Column(name="GMT_CODIGO")
	public Integer getGmtCodigo() {
		return this.gmtCodigo;
	}
	public void setGmtCodigo(Integer gmtCodigo) {
		this.gmtCodigo = gmtCodigo;
	}

	public Integer getCodigo() {
		return this.codigo;
	}
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ScoClassifMatNiv1Id)) {
			return false;
		}
		ScoClassifMatNiv1Id castOther = (ScoClassifMatNiv1Id)other;
		return 
			this.gmtCodigo.equals(castOther.gmtCodigo)
			&& this.codigo.equals(castOther.codigo);

    }
    
	@Override
	public int hashCode() {
		final Integer prime = 31;
		Integer hash = 17;
		hash = hash * prime + this.gmtCodigo.hashCode();
		hash = hash * prime + this.codigo.hashCode();
		
		return hash;
    }
}