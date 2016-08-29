package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the sco_progr_entrega_itens_af database table.
 * 
 */
@Embeddable
public class ScoProgEntregaItemAutorizacaoFornecimentoId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4318875592487505874L;
	private Integer iafAfnNumero;
	private Integer iafNumero;
	private Integer seq;
	private Integer parcela;

    public ScoProgEntregaItemAutorizacaoFornecimentoId() {
    }

	public ScoProgEntregaItemAutorizacaoFornecimentoId(Integer iafAfnNumero, Integer iafNumero, Integer seq, Integer parcela) {
		super();
		this.iafAfnNumero = iafAfnNumero;
		this.iafNumero = iafNumero;
		this.seq = seq;
		this.parcela = parcela;
	}

	@Column(name="IAF_AFN_NUMERO")
	public Integer getIafAfnNumero() {
		return this.iafAfnNumero;
	}
	public void setIafAfnNumero(Integer iafAfnNumero) {
		this.iafAfnNumero = iafAfnNumero;
	}

	@Column(name="IAF_NUMERO")
	public Integer getIafNumero() {
		return this.iafNumero;
	}
	public void setIafNumero(Integer iafNumero) {
		this.iafNumero = iafNumero;
	}

	@Column(name="SEQ")
	public Integer getSeq() {
		return this.seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name="PARCELA")
	public Integer getParcela() {
		return this.parcela;
	}
	public void setParcela(Integer parcela) {
		this.parcela = parcela;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ScoProgEntregaItemAutorizacaoFornecimentoId)) {
			return false;
		}
		ScoProgEntregaItemAutorizacaoFornecimentoId castOther = (ScoProgEntregaItemAutorizacaoFornecimentoId)other;
		return 
			this.iafAfnNumero.equals(castOther.iafAfnNumero)
			&& this.iafNumero.equals(castOther.iafNumero)
			&& this.seq.equals(castOther.seq)
			&& this.parcela.equals(castOther.parcela);

    }
    
	@Override
	public int hashCode() {
		final Integer prime = 31;
		Integer hash = 17;
		hash = hash * prime + this.iafAfnNumero.hashCode();
		hash = hash * prime + this.iafNumero.hashCode();
		hash = hash * prime + this.seq.hashCode();
		hash = hash * prime + this.parcela.hashCode();
		
		return hash;
    }
}