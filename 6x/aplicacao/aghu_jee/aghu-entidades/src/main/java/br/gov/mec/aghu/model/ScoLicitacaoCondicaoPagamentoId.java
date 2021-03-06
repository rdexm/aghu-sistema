package br.gov.mec.aghu.model;

// Generated 30/11/2011 14:04:22 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * ScoLicitacaoCondPagamentosId generated by hbm2java
 */
@Embeddable
public class ScoLicitacaoCondicaoPagamentoId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5922834917785524834L;

	private Integer lctNumero;
	private Short seq;

	public ScoLicitacaoCondicaoPagamentoId() {
	}

	public ScoLicitacaoCondicaoPagamentoId(Integer lctNumero, Short seq) {
		this.lctNumero = lctNumero;
		this.seq = seq;
	}

	@Column(name = "LCT_NUMERO", nullable = false, precision = 7, scale = 0)
	public Integer getLctNumero() {
		return this.lctNumero;
	}

	public void setLctNumero(Integer lctNumero) {
		this.lctNumero = lctNumero;
	}

	@Column(name = "SEQ", nullable = false, precision = 3, scale = 0)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((lctNumero == null) ? 0 : lctNumero.hashCode());
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
		ScoLicitacaoCondicaoPagamentoId other = (ScoLicitacaoCondicaoPagamentoId) obj;
		if (lctNumero == null) {
			if (other.lctNumero != null){
				return false;
			}
		} else if (!lctNumero.equals(other.lctNumero)){
			return false;
		}
		if (seq == null) {
			if (other.seq != null){
				return false;
			}
		} else if (!seq.equals(other.seq)){
			return false;
		}
		return true;
	}
}
