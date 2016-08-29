package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;


@Embeddable()
public class RapPessoaTipoInformacoesId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7727699997288488518L;
	private Integer pesCodigo;
	private Short tiiSeq;
	private Long seq;

	public RapPessoaTipoInformacoesId() {}

	public RapPessoaTipoInformacoesId(Integer pesCodigo, Short tiiSeq, Long seq) {
		this.pesCodigo = pesCodigo;
		this.tiiSeq = tiiSeq;
		this.seq = seq;
	}

	// getters & setters

	@Column(name = "PES_CODIGO", length = 9, nullable = false)
	public Integer getPesCodigo() {
		return this.pesCodigo;
	}

	public void setPesCodigo(Integer pesCodigo) {
		this.pesCodigo = pesCodigo;
	}

	@Column(name = "TII_SEQ", length = 4, nullable = false)
	public Short getTiiSeq() {
		return this.tiiSeq;
	}

	public void setTiiSeq(Short tiiSeq) {
		this.tiiSeq = tiiSeq;
	}

	@Column(name = "SEQ", length = 38, nullable = false)
	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("pesCodigo", this.pesCodigo)
				.append("tiiSeq", this.tiiSeq).append("seq", this.seq).toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((pesCodigo == null) ? 0 : pesCodigo.hashCode());
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		result = prime * result + ((tiiSeq == null) ? 0 : tiiSeq.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RapPessoaTipoInformacoesId other = (RapPessoaTipoInformacoesId) obj;
		if (pesCodigo == null) {
			if (other.pesCodigo != null) {
				return false;
			}
		} else if (!pesCodigo.equals(other.pesCodigo)) {
			return false;
		}
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		if (tiiSeq == null) {
			if (other.tiiSeq != null) {
				return false;
			}
		} else if (!tiiSeq.equals(other.tiiSeq)) {
			return false;
		}
		return true;
	}
}
