package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the sco_af_pedidos database table.
 * 
 */
@Embeddable
public class ScoAutorizacaoFornecedorPedidoId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3348476433817548153L;
	private Integer afnNumero;
	private Integer numero;

    public ScoAutorizacaoFornecedorPedidoId() {
    }

	public ScoAutorizacaoFornecedorPedidoId(final Integer afnNumero, final Integer numero) {
		super();
		this.afnNumero = afnNumero;
		this.numero = numero;
	}

	@Column(name="AFN_NUMERO")
	public Integer getAfnNumero() {
		return this.afnNumero;
	}
	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
	}

	@Column(name="NUMERO")
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
		if (!(other instanceof ScoAutorizacaoFornecedorPedidoId)) {
			return false;
		}
		ScoAutorizacaoFornecedorPedidoId castOther = (ScoAutorizacaoFornecedorPedidoId)other;
		return 
			this.afnNumero.equals(castOther.afnNumero)
			&& this.numero.equals(castOther.numero);

    }
    
	@Override
	public int hashCode() {
		final Integer prime = 31;
		Integer hash = 17;
		hash = hash * prime + this.afnNumero.hashCode();
		hash = hash * prime + this.numero.hashCode();
		
		return hash;
    }
}