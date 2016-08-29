package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the sco_classif_mat_niv2 database table.
 * 
 */
@Embeddable
public class ScoClassifMatNiv2Id implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8475486545949695041L;
	private Integer cn1GmtCodigo;
	private Integer cn1Codigo;
	private Integer codigo;

    public ScoClassifMatNiv2Id() {
    }

	@Column(name="CN1_GMT_CODIGO")
	public Integer getCn1GmtCodigo() {
		return this.cn1GmtCodigo;
	}
	public void setCn1GmtCodigo(Integer cn1GmtCodigo) {
		this.cn1GmtCodigo = cn1GmtCodigo;
	}

	@Column(name="CN1_CODIGO")
	public Integer getCn1Codigo() {
		return this.cn1Codigo;
	}
	public void setCn1Codigo(Integer cn1Codigo) {
		this.cn1Codigo = cn1Codigo;
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
		if (!(other instanceof ScoClassifMatNiv2Id)) {
			return false;
		}
		ScoClassifMatNiv2Id castOther = (ScoClassifMatNiv2Id)other;
		return 
			this.cn1GmtCodigo.equals(castOther.cn1GmtCodigo)
			&& this.cn1Codigo.equals(castOther.cn1Codigo)
			&& this.codigo.equals(castOther.codigo);

    }
    
	@Override
	public int hashCode() {
		final Integer prime = 31;
		Integer hash = 17;
		hash = hash * prime + this.cn1GmtCodigo.hashCode();
		hash = hash * prime + this.cn1Codigo.hashCode();
		hash = hash * prime + this.codigo.hashCode();
		
		return hash;
    }
}