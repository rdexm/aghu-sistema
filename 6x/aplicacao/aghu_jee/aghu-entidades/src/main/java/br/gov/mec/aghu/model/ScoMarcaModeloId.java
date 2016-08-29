package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the sco_marca_modelos database table.
 * 
 */
@Embeddable
public class ScoMarcaModeloId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7339630872275168221L;
	private Integer mcmCodigo;
	private Integer seqp;

    public ScoMarcaModeloId() {
    }

	@Column(name="MCM_CODIGO")
	public Integer getMcmCodigo() {
		return this.mcmCodigo;
	}
	public void setMcmCodigo(Integer mcmCodigo) {
		this.mcmCodigo = mcmCodigo;
	}

	public Integer getSeqp() {
		return this.seqp;
	}
	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ScoMarcaModeloId)) {
			return false;
		}
		ScoMarcaModeloId castOther = (ScoMarcaModeloId)other;
		return 
			this.mcmCodigo.equals(castOther.mcmCodigo)
			&& this.seqp.equals(castOther.seqp);

    }
    
	@Override
	public int hashCode() {
		final Integer prime = 31;
		Integer hash = 17;
		hash = hash * prime + (this.mcmCodigo != null ? this.mcmCodigo.hashCode() : 0);
		hash = hash * prime + (this.seqp != null ? this.seqp.hashCode() :0);
		
		return hash;
    }
}