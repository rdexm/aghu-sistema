package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the sce_inr_idf database table.
 * 
 */
@Embeddable
public class SceItemNotaRecebimentoDevolucaoFornecedorId implements EntityCompositeId {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4315301459990031208L;
	//default serial version id, required for serializable classes.	
	private Integer iafAfnNumero;
	private Integer iafNumero;
	private Integer nrsSeq;
	private Integer dfsSeq;
	private Integer idfNumero;

	
    public SceItemNotaRecebimentoDevolucaoFornecedorId() {
    }

	@Column(name="INR_IAF_AFN_NUMERO", nullable=false)
	public Integer getIafAfnNumero() {
		return iafAfnNumero;
	}


	public void setIafAfnNumero(Integer iafAfnNumero) {
		this.iafAfnNumero = iafAfnNumero;
	}

	@Column(name="INR_IAF_NUMERO", nullable=false)
	public Integer getIafNumero() {
		return iafNumero;
	}


	public void setIafNumero(Integer iafNumero) {
		this.iafNumero = iafNumero;
	}

	@Column(name="INR_NRS_SEQ", nullable=false)
	public Integer getNrsSeq() {
		return nrsSeq;
	}


	public void setNrsSeq(Integer nrsSeq) {
		this.nrsSeq = nrsSeq;
	}

	@Column(name="IDF_DFS_SEQ", nullable=false)
	public Integer getDfsSeq() {
		return dfsSeq;
	}

	
	public void setDfsSeq(Integer dfsSeq) {
		this.dfsSeq = dfsSeq;
	}

	@Column(name="IDF_NUMERO", nullable=false)
	public Integer getIdfNumero() {
		return idfNumero;
	}


	public void setIdfNumero(Integer idfNumero) {
		this.idfNumero = idfNumero;
	}


	@Override
	public boolean equals(Object other) {
		boolean result = false;
		if (this == other) {
			return true;
		}
		if (!(other instanceof SceItemNotaRecebimentoDevolucaoFornecedorId)) {
			return false;
		}
		SceItemNotaRecebimentoDevolucaoFornecedorId castOther = (SceItemNotaRecebimentoDevolucaoFornecedorId)other;
		
		result = iafAfnNumero.equals(castOther.iafAfnNumero) &&
				iafNumero.equals(castOther.iafNumero) &&
				nrsSeq.equals(castOther.nrsSeq) &&
				dfsSeq.equals(castOther.dfsSeq) &&
				idfNumero.equals(castOther.idfNumero);
		
		return result;

    }
    
	@Override
	public int hashCode() {
		final Integer prime = 31;
		Integer hash = 17;
		hash = hash * prime + this.iafAfnNumero.hashCode();
		hash = hash * prime + this.iafNumero.hashCode();
		hash = hash * prime + this.nrsSeq.hashCode();
		hash = hash * prime + this.dfsSeq.hashCode();
		hash = hash * prime + this.idfNumero.hashCode();
		
		return hash;
    }
}