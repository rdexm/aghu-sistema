package br.gov.mec.aghu.model;

// Generated 08/02/2010 17:25:25 by Hibernate Tools 3.2.5.Beta

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * AelPacUnidFuncionaisId generated by hbm2java
 */

@Embeddable
public class AelPacUnidFuncionaisId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3864412196937989030L;
	private Integer piuPacCodigo;
	private AghUnidadesFuncionais unidadeFuncional;
	private Integer seqp;

	public AelPacUnidFuncionaisId() {
	}

	public AelPacUnidFuncionaisId(Integer piuPacCodigo, AghUnidadesFuncionais unidadeFuncional, Integer seqp) {
		this.piuPacCodigo = piuPacCodigo;
		this.unidadeFuncional = unidadeFuncional;
		this.seqp = seqp;
	}

	@Column(name = "PIU_PAC_CODIGO", nullable = false, precision = 8, scale = 0)
	public Integer getPiuPacCodigo() {
		return this.piuPacCodigo;
	}

	public void setPiuPacCodigo(Integer piuPacCodigo) {
		this.piuPacCodigo = piuPacCodigo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PIU_UNF_SEQ", nullable = false)
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return this.unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	@Column(name = "SEQP", nullable = false, precision = 8, scale = 0)
	public Integer getSeqp() {
		return this.seqp;
	}

	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}

	@Override
	public boolean equals(Object other) {
		if ((this == other)) {
			return true;
		}
		if ((other == null)) {
			return false;
		}
		if (!(other instanceof AelPacUnidFuncionaisId)) {
			return false;
		}
		AelPacUnidFuncionaisId castOther = (AelPacUnidFuncionaisId) other;

		return this.getPiuPacCodigo() != null && this.getPiuPacCodigo().equals(castOther.getPiuPacCodigo()) &&
				this.getUnidadeFuncional() != null && this.getUnidadeFuncional().equals(castOther.getUnidadeFuncional()) &&
				this.getSeqp() != null && this.getSeqp().equals(castOther.getSeqp());
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 37 * result + (this.getPiuPacCodigo() == null ? 0 : this.getPiuPacCodigo().hashCode());
		result = 37 * result + (this.getUnidadeFuncional() == null ? 0 : this.getUnidadeFuncional().hashCode());
		result = 37 * result + (this.getSeqp() == null ? 0 : this.getSeqp().hashCode());
		return result;
	}

}
