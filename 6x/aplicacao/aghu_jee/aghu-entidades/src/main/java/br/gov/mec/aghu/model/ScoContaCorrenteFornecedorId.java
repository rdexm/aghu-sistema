package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the sco_conta_corrente_fornecedor database table.
 * 
 */
@Embeddable
public class ScoContaCorrenteFornecedorId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -263779258632769124L;
	private Integer frnNumero;
	private Integer agbBcoCodigo;
	private Integer agbCodigo;
	private String contaCorrente;

    public ScoContaCorrenteFornecedorId() {
    }

	@Column(name="FRN_NUMERO")
	public Integer getFrnNumero() {
		return this.frnNumero;
	}
	public void setFrnNumero(Integer frnNumero) {
		this.frnNumero = frnNumero;
	}

	@Column(name="AGB_BCO_CODIGO")
	public Integer getAgbBcoCodigo() {
		return this.agbBcoCodigo;
	}
	public void setAgbBcoCodigo(Integer agbBcoCodigo) {
		this.agbBcoCodigo = agbBcoCodigo;
	}

	@Column(name="AGB_CODIGO")
	public Integer getAgbCodigo() {
		return this.agbCodigo;
	}
	public void setAgbCodigo(Integer agbCodigo) {
		this.agbCodigo = agbCodigo;
	}

	@Column(name="CONTA_CORRENTE")
	public String getContaCorrente() {
		return this.contaCorrente;
	}
	public void setContaCorrente(String contaCorrente) {
		this.contaCorrente = contaCorrente;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ScoContaCorrenteFornecedorId)) {
			return false;
		}
		ScoContaCorrenteFornecedorId castOther = (ScoContaCorrenteFornecedorId)other;
		return 
			this.frnNumero.equals(castOther.frnNumero)
			&& this.agbBcoCodigo.equals(castOther.agbBcoCodigo)
			&& this.agbCodigo.equals(castOther.agbCodigo)
			&& this.contaCorrente.equals(castOther.contaCorrente);

    }
    
	@Override
	public int hashCode() {
		final Integer prime = 31;
		Integer hash = 17;
		hash = hash * prime + this.frnNumero.hashCode();
		hash = hash * prime + this.agbBcoCodigo.hashCode();
		hash = hash * prime + this.agbCodigo.hashCode();
		hash = hash * prime + this.contaCorrente.hashCode();
		
		return hash;
    }
}