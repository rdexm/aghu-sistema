package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the sce_item_dfs database table.
 * 
 */
@Embeddable
public class SceItemDevolucaoFornecedorId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3824324859890481868L;
	private Integer numero;
	private Integer dfsSeq;

    public SceItemDevolucaoFornecedorId() {
    }

    @Column(name="DFS_SEQ")
    public Integer getDfsSeq() {
		return dfsSeq;
	}

	public void setDfsSeq(Integer dfsSeq) {
		this.dfsSeq = dfsSeq;
	}

    
	public Integer getNumero() {
		return this.numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof SceItemDevolucaoFornecedorId)) {
			return false;
		}
		SceItemDevolucaoFornecedorId castOther = (SceItemDevolucaoFornecedorId)other;
		return 
			this.dfsSeq.equals(castOther.dfsSeq)
			&& this.numero.equals(castOther.numero);

    }
    
	@Override
	public int hashCode() {
		final Integer prime = 31;
		Integer hash = 17;
		hash = hash * prime + this.dfsSeq.hashCode();
		hash = hash * prime + this.numero.hashCode();
		
		return hash;
    }
}