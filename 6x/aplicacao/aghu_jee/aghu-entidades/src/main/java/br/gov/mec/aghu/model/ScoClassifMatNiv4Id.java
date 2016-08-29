package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the sco_classif_mat_niv4 database table.
 * 
 */
@Embeddable
public class ScoClassifMatNiv4Id implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6364888271621851560L;
	private Integer cn3Cn2Cn1GmtCodigo;
	private Integer cn3Cn2Cn1Codigo;
	private Integer cn3Cn2Codigo;
	private Integer cn3Codigo;
	private Integer codigo;

    public ScoClassifMatNiv4Id() {
    }

	@Column(name="CN3_CN2_CN1_GMT_CODIGO")
	public Integer getCn3Cn2Cn1GmtCodigo() {
		return this.cn3Cn2Cn1GmtCodigo;
	}
	public void setCn3Cn2Cn1GmtCodigo(Integer cn3Cn2Cn1GmtCodigo) {
		this.cn3Cn2Cn1GmtCodigo = cn3Cn2Cn1GmtCodigo;
	}

	@Column(name="CN3_CN2_CN1_CODIGO")
	public Integer getCn3Cn2Cn1Codigo() {
		return this.cn3Cn2Cn1Codigo;
	}
	public void setCn3Cn2Cn1Codigo(Integer cn3Cn2Cn1Codigo) {
		this.cn3Cn2Cn1Codigo = cn3Cn2Cn1Codigo;
	}

	@Column(name="CN3_CN2_CODIGO")
	public Integer getCn3Cn2Codigo() {
		return this.cn3Cn2Codigo;
	}
	public void setCn3Cn2Codigo(Integer cn3Cn2Codigo) {
		this.cn3Cn2Codigo = cn3Cn2Codigo;
	}

	@Column(name="CN3_CODIGO")
	public Integer getCn3Codigo() {
		return this.cn3Codigo;
	}
	public void setCn3Codigo(Integer cn3Codigo) {
		this.cn3Codigo = cn3Codigo;
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
		if (!(other instanceof ScoClassifMatNiv4Id)) {
			return false;
		}
		ScoClassifMatNiv4Id castOther = (ScoClassifMatNiv4Id)other;
		return 
			this.cn3Cn2Cn1GmtCodigo.equals(castOther.cn3Cn2Cn1GmtCodigo)
			&& this.cn3Cn2Cn1Codigo.equals(castOther.cn3Cn2Cn1Codigo)
			&& this.cn3Cn2Codigo.equals(castOther.cn3Cn2Codigo)
			&& this.cn3Codigo.equals(castOther.cn3Codigo)
			&& this.codigo.equals(castOther.codigo);

    }
    
	@Override
	public int hashCode() {
		final Integer prime = 31;
		Integer hash = 17;
		hash = hash * prime + this.cn3Cn2Cn1GmtCodigo.hashCode();
		hash = hash * prime + this.cn3Cn2Cn1Codigo.hashCode();
		hash = hash * prime + this.cn3Cn2Codigo.hashCode();
		hash = hash * prime + this.cn3Codigo.hashCode();
		hash = hash * prime + this.codigo.hashCode();
		
		return hash;
    }
}