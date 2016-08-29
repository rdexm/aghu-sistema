package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the sco_marca_modelo_materiais database table.
 * 
 */
@Embeddable
public class ScoMarcaModeloMaterialId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9094543024133745090L;
	private Integer matCodigo;
	private Integer momMcmCodigo;
	private Integer momSeqp;

    public ScoMarcaModeloMaterialId() {
    }

	@Column(name="MAT_CODIGO")
	public Integer getMatCodigo() {
		return this.matCodigo;
	}
	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	@Column(name="MOM_MCM_CODIGO")
	public Integer getMomMcmCodigo() {
		return this.momMcmCodigo;
	}
	public void setMomMcmCodigo(Integer momMcmCodigo) {
		this.momMcmCodigo = momMcmCodigo;
	}

	@Column(name="MOM_SEQP")
	public Integer getMomSeqp() {
		return this.momSeqp;
	}
	public void setMomSeqp(Integer momSeqp) {
		this.momSeqp = momSeqp;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ScoMarcaModeloMaterialId)) {
			return false;
		}
		ScoMarcaModeloMaterialId castOther = (ScoMarcaModeloMaterialId)other;
		return 
			this.matCodigo.equals(castOther.matCodigo)
			&& this.momMcmCodigo.equals(castOther.momMcmCodigo)
			&& this.momSeqp.equals(castOther.momSeqp);

    }
    
	@Override
	public int hashCode() {
		final Integer prime = 31;
		Integer hash = 17;
		hash = hash * prime + this.matCodigo.hashCode();
		hash = hash * prime + this.momMcmCodigo.hashCode();
		hash = hash * prime + this.momSeqp.hashCode();
		
		return hash;
    }
}