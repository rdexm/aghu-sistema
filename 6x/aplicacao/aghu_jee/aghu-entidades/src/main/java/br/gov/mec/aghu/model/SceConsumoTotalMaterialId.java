package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable	
public class SceConsumoTotalMaterialId implements EntityCompositeId{
	//default serial version id, required for serializable classes.
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3130190119239613009L;
	private Date dtCompetencia;
	private Short almSeq;
	private Integer fccCodigo;
	private Integer matCodigo;

    public SceConsumoTotalMaterialId() {
    }

    @Temporal( TemporalType.TIMESTAMP)
	@Column(name="DT_COMPETENCIA")
	public Date getDtCompetencia() {
		return this.dtCompetencia;
	}
	public void setDtCompetencia(Date dtCompetencia) {
		this.dtCompetencia = dtCompetencia;
	}

	@Column(name="ALM_SEQ")
	public Short getAlmSeq() {
		return this.almSeq;
	}
	public void setAlmSeq(Short almSeq) {
		this.almSeq = almSeq;
	}
	
	@Column(name="CCT_CODIGO")
	public Integer getFccCodigo() {
		return fccCodigo;
	}

	public void setFccCodigo(Integer fccCodigo) {
		this.fccCodigo = fccCodigo;
	}

	@Column(name="MAT_CODIGO")
	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}
	

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getMatCodigo());
		umHashCodeBuilder.append(this.getDtCompetencia());
		umHashCodeBuilder.append(this.getAlmSeq());
		umHashCodeBuilder.append(this.getFccCodigo());
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
		if (!(obj instanceof SceConsumoTotalMaterialId)) {
			return false;
		}
		SceConsumoTotalMaterialId other = (SceConsumoTotalMaterialId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getMatCodigo(), other.getMatCodigo());
		umEqualsBuilder.append(this.getDtCompetencia(), other.getDtCompetencia());
		umEqualsBuilder.append(this.getAlmSeq(), other.getAlmSeq());
		umEqualsBuilder.append(this.getFccCodigo(), other.getFccCodigo());
		return umEqualsBuilder.isEquals();
	}
	// ##### GeradorEqualsHashCodeMain #####
	
}
