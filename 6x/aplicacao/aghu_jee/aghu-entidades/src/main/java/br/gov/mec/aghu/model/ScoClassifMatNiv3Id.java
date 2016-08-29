package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the sco_classif_mat_niv3 database table.
 * 
 */
@Embeddable
public class ScoClassifMatNiv3Id implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6789600938539311030L;
	private Integer cn2Cn1GmtCodigo;
	private Integer cn2Cn1Codigo;
	private Integer cn2Codigo;
	private Integer codigo;

    public ScoClassifMatNiv3Id() {
    }

	@Column(name="CN2_CN1_GMT_CODIGO")
	public Integer getCn2Cn1GmtCodigo() {
		return this.cn2Cn1GmtCodigo;
	}
	public void setCn2Cn1GmtCodigo(Integer cn2Cn1GmtCodigo) {
		this.cn2Cn1GmtCodigo = cn2Cn1GmtCodigo;
	}

	@Column(name="CN2_CN1_CODIGO")
	public Integer getCn2Cn1Codigo() {
		return this.cn2Cn1Codigo;
	}
	public void setCn2Cn1Codigo(Integer cn2Cn1Codigo) {
		this.cn2Cn1Codigo = cn2Cn1Codigo;
	}

	@Column(name="CN2_CODIGO")
	public Integer getCn2Codigo() {
		return this.cn2Codigo;
	}
	public void setCn2Codigo(Integer cn2Codigo) {
		this.cn2Codigo = cn2Codigo;
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
		if (!(other instanceof ScoClassifMatNiv3Id)) {
			return false;
		}
		ScoClassifMatNiv3Id castOther = (ScoClassifMatNiv3Id)other;
		return 
			this.cn2Cn1GmtCodigo.equals(castOther.cn2Cn1GmtCodigo)
			&& this.cn2Cn1Codigo.equals(castOther.cn2Cn1Codigo)
			&& this.cn2Codigo.equals(castOther.cn2Codigo)
			&& this.codigo.equals(castOther.codigo);

    }
    
	@Override
	public int hashCode() {
		final Integer prime = 31;
		Integer hash = 17;
		hash = hash * prime + this.cn2Cn1GmtCodigo.hashCode();
		hash = hash * prime + this.cn2Cn1Codigo.hashCode();
		hash = hash * prime + this.cn2Codigo.hashCode();
		hash = hash * prime + this.codigo.hashCode();
		
		return hash;
    }
}