package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class ScoFornecedorIrregularFiscalId implements EntityCompositeId {

	/**
	 * serialVersionUID auto-generation
	 */
	private static final long serialVersionUID = -7263984589750600403L;
	private Integer frnNumero;
	private Integer numero;

	public ScoFornecedorIrregularFiscalId() {
	}

	public ScoFornecedorIrregularFiscalId(Integer frnNumero, Integer numero) {
		this.frnNumero = frnNumero;
		this.numero = numero;
	}

	@Column(name = "FRN_NUMERO", nullable = false)
	public Integer getFrnNumero() {
		return this.frnNumero;
	}
	
	public void setFrnNumero(Integer frnNumero) {
		this.frnNumero = frnNumero;
	}
	
	@Column(name = "NUMERO", nullable = false)
	public Integer getNumero() {
		return this.numero;
	}
	
	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	@Override
	public boolean equals(Object other) {
		if ((this == other)) {
			return true;
		}
		if ((other == null)) {
			return false;
		}
		if (!(other instanceof ScoFornecedorIrregularFiscalId)) {
			return false;
		}
		ScoFornecedorIrregularFiscalId castOther = (ScoFornecedorIrregularFiscalId) other;

		return (this.getFrnNumero() == castOther.getFrnNumero())
				&& (this.getNumero() == castOther.getNumero());
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getFrnNumero();
		result = 37 * result + this.getNumero();
		return result;
	}

}
