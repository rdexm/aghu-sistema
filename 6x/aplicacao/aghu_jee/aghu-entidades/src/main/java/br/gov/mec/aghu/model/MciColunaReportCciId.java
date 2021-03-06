package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Embeddable;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * MciColunaReportCciId generated by hbm2java
 */
@Embeddable
public class MciColunaReportCciId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1033841079319453203L;
	private String pmcAplicacaoCodigo;
	private Short ncrSeq;

	public MciColunaReportCciId() {
	}

	public MciColunaReportCciId(String pmcAplicacaoCodigo, Short ncrSeq) {
		this.pmcAplicacaoCodigo = pmcAplicacaoCodigo;
		this.ncrSeq = ncrSeq;
	}

	@Column(name = "PMC_APLICACAO_CODIGO", nullable = false, length = 20)
	@Length(max = 20)
	public String getPmcAplicacaoCodigo() {
		return this.pmcAplicacaoCodigo;
	}

	public void setPmcAplicacaoCodigo(String pmcAplicacaoCodigo) {
		this.pmcAplicacaoCodigo = pmcAplicacaoCodigo;
	}

	@Column(name = "NCR_SEQ", nullable = false)
	public Short getNcrSeq() {
		return this.ncrSeq;
	}

	public void setNcrSeq(Short ncrSeq) {
		this.ncrSeq = ncrSeq;
	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getPmcAplicacaoCodigo());
		umHashCodeBuilder.append(this.getNcrSeq());
		return umHashCodeBuilder.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MciColunaReportCciId)) {
			return false;
		}
		MciColunaReportCciId other = (MciColunaReportCciId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getPmcAplicacaoCodigo(), other.getPmcAplicacaoCodigo());
		umEqualsBuilder.append(this.getNcrSeq(), other.getNcrSeq());
		return umEqualsBuilder.isEquals();
	}
	// ##### GeradorEqualsHashCodeMain #####
	
}
