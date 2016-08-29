package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;


@Embeddable
public class ScoItemLicitacaoId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3475373970325073149L;
	private Integer lctNumero;
	private Short numero;

	// construtores
	public ScoItemLicitacaoId() {
	}

	public ScoItemLicitacaoId(Integer lctNumero, Short numero) {
		this.lctNumero = lctNumero;
		this.numero = numero;
	}

	// getters & setters
	@Column(name = "LCT_NUMERO", length = 7, nullable = false)
	public Integer getLctNumero() {
		return lctNumero;
	}

	public void setLctNumero(Integer lctNumero) {
		this.lctNumero = lctNumero;
	}
	
	@Column(name = "NUMERO", length = 3, nullable = false)
	public Short getNumero() {
		return this.numero;
	}

	public void setNumero(Short numero) {
		this.numero = numero;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((lctNumero == null) ? 0 : lctNumero.hashCode());
		result = prime * result + ((numero == null) ? 0 : numero.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		ScoItemLicitacaoId other = (ScoItemLicitacaoId) obj;
		if (lctNumero == null) {
			if (other.lctNumero != null){
				return false;
			}
		} else if (!lctNumero.equals(other.lctNumero)){
			return false;
		}
		if (numero == null) {
			if (other.numero != null){
				return false;
			}
		} else if (!numero.equals(other.numero)){
			return false;
		}
		return true;
	}
}
