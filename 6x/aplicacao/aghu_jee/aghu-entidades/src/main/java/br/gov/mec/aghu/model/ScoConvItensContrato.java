package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Digits;


import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * The persistent class for the sco_conv_itens_contratos database table.
 * 
 */
@Entity
@Table(name = "SCO_CONV_ITENS_CONTRATOS", schema = "AGH")
public class ScoConvItensContrato extends BaseEntityId<ScoConvItensContratoId> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 560032010339972576L;

	private ScoConvItensContratoId id;

	private BigDecimal valorRateio;

	private FsoConveniosFinanceiro fsoConveniosFinanceiro;

	private ScoItensContrato scoItensContrato;

	private boolean editando = false;

	private Integer version;

	public ScoConvItensContrato() {
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "iconSeq", column = @Column(name = "ICON_SEQ", nullable = false)),
			@AttributeOverride(name = "cvfCodigo", column = @Column(name = "CVF_CODIGO", nullable = false)) })
	public ScoConvItensContratoId getId() {
		return this.id;
	}

	public void setId(ScoConvItensContratoId id) {
		this.id = id;
	}

	@Column(name = "VALOR_RATEIO")
	@Digits(integer = 13, fraction = 2, message = "Valor rateio no máximo 13 números inteiros e 2 decimais")
	public BigDecimal getValorRateio() {
		return this.valorRateio;
	}

	public void setValorRateio(BigDecimal valorRateio) {
		this.valorRateio = valorRateio;
	}

	// bi-directional many-to-one association to FsoConveniosFinanceiro
	@ManyToOne
	@JoinColumn(name = "CVF_CODIGO", nullable = false, insertable = false, updatable = false)
	public FsoConveniosFinanceiro getFsoConveniosFinanceiro() {
		return this.fsoConveniosFinanceiro;
	}

	public void setFsoConveniosFinanceiro(
			FsoConveniosFinanceiro fsoConveniosFinanceiro) {
		this.fsoConveniosFinanceiro = fsoConveniosFinanceiro;
	}

	// bi-directional many-to-one association to ScoItensContrato
	@ManyToOne
	@JoinColumn(name = "ICON_SEQ", nullable = false, insertable = false, updatable = false)
	public ScoItensContrato getScoItensContrato() {
		return this.scoItensContrato;
	}

	public void setScoItensContrato(ScoItensContrato scoItensContrato) {
		this.scoItensContrato = scoItensContrato;
	}

	@Transient
	public boolean isEditando() {
		return editando;
	}

	public void setEditando(boolean editando) {
		this.editando = editando;
	}

	public enum Fields {
		ITEM_CONT_SEQ("id.iconSeq"), FSO_CONV_FINANC ("fsoConveniosFinanceiro");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	@Column(name = "VERSION", length = 7)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
		if (!(obj instanceof ScoConvItensContrato)) {
			return false;
		}
		ScoConvItensContrato other = (ScoConvItensContrato) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}