package br.gov.mec.aghu.model;

// Generated 18/03/2010 16:55:22 by Hibernate Tools 3.3.0.GA

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class VMbcProfServidorId implements EntityCompositeId {

	private static final long serialVersionUID = 1464310277727324204L;
	
	private Integer serMatricula;
	private Short serVinCodigo;
	private Short unfSeq;
	private DominioFuncaoProfissional indFuncaoProf;
	
	public VMbcProfServidorId() {
	}

	public VMbcProfServidorId(Integer serMatricula, Short serVinCodigo,
			Short unfSeq, DominioFuncaoProfissional indFuncaoProf) {
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.unfSeq = unfSeq;
		this.indFuncaoProf = indFuncaoProf;
	}

	@Column(name = "SER_MATRICULA", nullable = false, precision = 7, scale = 0)
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false, precision = 3, scale = 0)
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "PUC_UNF_SEQ", nullable = false, precision = 4, scale = 0)
	public Short getUnfSeq() {
		return this.unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	@Column(name = "PUC_IND_FUNCAO_PROF", nullable = false, length = 3)
	@Enumerated(EnumType.STRING)
	public DominioFuncaoProfissional getIndFuncaoProf() {
		return this.indFuncaoProf;
	}

	public void setIndFuncaoProf(DominioFuncaoProfissional indFuncaoProf) {
		this.indFuncaoProf = indFuncaoProf;
	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof VMbcProfServidorId)) {
			return false;
		}
		VMbcProfServidorId other = (VMbcProfServidorId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getSerMatricula(), other.getSerMatricula());
		umEqualsBuilder.append(this.getSerVinCodigo(), other.getSerVinCodigo());
		umEqualsBuilder.append(this.getUnfSeq(), other.getUnfSeq());
		umEqualsBuilder.append(this.getIndFuncaoProf(), other.getIndFuncaoProf());
		return umEqualsBuilder.isEquals();
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getSerMatricula());
		umHashCodeBuilder.append(this.getSerVinCodigo());
		umHashCodeBuilder.append(this.getUnfSeq());
		umHashCodeBuilder.append(this.getIndFuncaoProf());
		return umHashCodeBuilder.toHashCode();
	}	

}
