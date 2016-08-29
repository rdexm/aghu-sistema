package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;


import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the sco_conv_itens_contratos database table.
 * 
 */
@Embeddable
public class ScoConvItensContratoId implements EntityCompositeId {


	/**
	 * 
	 */
	private static final long serialVersionUID = -2593140933762690040L;

	//default serial version id, required for serializable classes.
	@Column(name="ICON_SEQ",unique=true, nullable=false, precision=7)
	private Integer iconSeq;

	@Column(name="CVF_CODIGO",unique=true, nullable=false, precision=7)
	private Integer cvfCodigo;

    public ScoConvItensContratoId() {
    }
    
	public ScoConvItensContratoId(Integer iconSeq, Integer cvfCodigo) {
		this.iconSeq = iconSeq;
		this.cvfCodigo = cvfCodigo;
	}
	
	public Integer getIconSeq() {
		return this.iconSeq;
	}
	public void setIconSeq(Integer iconSeq) {
		this.iconSeq = iconSeq;
	}
	public Integer getCvfCodigo() {
		return this.cvfCodigo;
	}
	public void setCvfCodigo(Integer cvfCodigo) {
		this.cvfCodigo = cvfCodigo;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ScoConvItensContratoId)) {
			return false;
		}
		ScoConvItensContratoId castOther = (ScoConvItensContratoId)other;
		return 
			this.iconSeq.equals(castOther.iconSeq)
			&& this.cvfCodigo.equals(castOther.cvfCodigo);

    }
    
	@Override
	public int hashCode() {
		final Integer prime = 31;
		Integer hash = 17;
		hash = hash * prime + this.iconSeq.hashCode();
		hash = hash * prime + this.cvfCodigo.hashCode();
		
		return hash;
    }
}