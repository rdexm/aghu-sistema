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
public class Rhfp0285Id  implements EntityCompositeId{
	/**
	 * #42229 Incluido Starh Dependente
	 * 
	 */
	private static final long serialVersionUID = -2402041098086235307L;
	private Integer codContrato;
	private Integer codPlanoSaude;
	private Date dataInicio;
	private Integer codFunc;
	private Integer codPessoa;
	// construtores
	public Rhfp0285Id() {
	}
	public Rhfp0285Id(Integer codContrato, Integer codPlanoSaude, Date dataInicio) {
		this.codContrato = codContrato;
		this.codPlanoSaude = codPlanoSaude;
		this.dataInicio = dataInicio;
	}	
	@Column(name = "COD_CONTRATO",nullable = false)
	public Integer getCodContrato() {
		return codContrato;
	}

	public void setCodContrato(Integer codContrato) {
		this.codContrato = codContrato;
	}

	@Column(name = "COD_PLANO_SAUDE",nullable = false)
	public Integer getCodPlanoSaude() {
		return codPlanoSaude;
	}

	public void setCodPlanoSaude(Integer codPlanoSaude) {
		this.codPlanoSaude = codPlanoSaude;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_INICIO", length = 7,nullable = false)
	public Date getDataInicio() {
		return dataInicio;
	}
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}
	@Column(name = "COD_FUNC",nullable = false)
	public Integer getCodFunc() {
		return codFunc;
	}
	public void setCodFunc(Integer codFunc) {
		this.codFunc = codFunc;
	}
	@Column(name = "COD_PESSOA",nullable = false)
	public Integer getCodPessoa() {
		return codPessoa;
	}
	public void setCodPessoa(Integer codPessoa) {
		this.codPessoa = codPessoa;
	}
	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getCodContrato());
		umHashCodeBuilder.append(this.getCodPlanoSaude());
		umHashCodeBuilder.append(this.getDataInicio());
		umHashCodeBuilder.append(this.getCodFunc());
		umHashCodeBuilder.append(this.getCodPessoa());
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
		if (!(obj instanceof Rhfp0285Id)) {
			return false;
		}
		Rhfp0285Id other = (Rhfp0285Id) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getCodContrato(), other.getCodContrato());
		umEqualsBuilder.append(this.getCodPlanoSaude(), other.getCodPlanoSaude());
		umEqualsBuilder.append(this.getDataInicio(), other.getDataInicio());
		umEqualsBuilder.append(this.getCodFunc(), other.getCodFunc());
		umEqualsBuilder.append(this.getCodPessoa(), other.getCodPessoa());
		return umEqualsBuilder.isEquals();
	}
		
}
