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
public class Rhfp0283Id  implements EntityCompositeId{
	/**
	 * #42229 Incluido Starh 
	 * 
	 */
	private static final long serialVersionUID = -2402041098086235307L;
	private Integer codContrato;
	private Integer codPlanoSaude;
	private Date dataInicio;
	// construtores
	public Rhfp0283Id() {
	}
	public Rhfp0283Id(Integer codContrato, Integer codPlanoSaude, Date dataInicio) {
		this.codContrato = codContrato;
		this.codPlanoSaude = codPlanoSaude;
		this.dataInicio = dataInicio;
	}	
	@Column(name = "COD_CONTRATO")
	public Integer getCodContrato() {
		return codContrato;
	}

	public void setCodContrato(Integer codContrato) {
		this.codContrato = codContrato;
	}

	@Column(name = "COD_PLANO_SAUDE")
	public Integer getCodPlanoSaude() {
		return codPlanoSaude;
	}

	public void setCodPlanoSaude(Integer codPlanoSaude) {
		this.codPlanoSaude = codPlanoSaude;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_INICIO", length = 7)
	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getCodContrato());
		umHashCodeBuilder.append(this.getCodPlanoSaude());
		umHashCodeBuilder.append(this.getDataInicio());
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
		if (!(obj instanceof Rhfp0283Id)) {
			return false;
		}
		Rhfp0283Id other = (Rhfp0283Id) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getCodContrato(), other.getCodContrato());
		umEqualsBuilder.append(this.getCodPlanoSaude(), other.getCodPlanoSaude());
		umEqualsBuilder.append(this.getDataInicio(), other.getDataInicio());
		return umEqualsBuilder.isEquals();
	}
		
}
