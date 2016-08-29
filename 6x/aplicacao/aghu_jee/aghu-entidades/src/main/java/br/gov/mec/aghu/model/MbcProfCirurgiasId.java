package br.gov.mec.aghu.model;

// Generated 19/03/2010 17:25:07 by Hibernate Tools 3.2.5.Beta

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * MbcProfCirurgiasId generated by hbm2java
 */
@Embeddable
public class MbcProfCirurgiasId implements EntityCompositeId {
	private static final long serialVersionUID = 4041023008458739661L;
	
	
	private Integer crgSeq;
	private Integer pucSerMatricula;
	private Short pucSerVinCodigo;
	private Short pucUnfSeq;
	private DominioFuncaoProfissional pucIndFuncaoProf;

	public MbcProfCirurgiasId() {
	}

	public MbcProfCirurgiasId(Integer crgSeq, Integer pucSerMatricula,
			Short pucSerVinCodigo, Short pucUnfSeq, DominioFuncaoProfissional pucIndFuncaoProf) {
		this.crgSeq = crgSeq;
		this.pucSerMatricula = pucSerMatricula;
		this.pucSerVinCodigo = pucSerVinCodigo;
		this.pucUnfSeq = pucUnfSeq;
		this.pucIndFuncaoProf = pucIndFuncaoProf;
	}

	@Column(name = "CRG_SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getCrgSeq() {
		return this.crgSeq;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}
	
	
	@Column(name = "PUC_SER_MATRICULA", nullable = false, precision = 7, scale = 0)
	public Integer getPucSerMatricula() {
		return this.pucSerMatricula;
	}

	public void setPucSerMatricula(Integer pucSerMatricula) {
		this.pucSerMatricula = pucSerMatricula;
	}

	@Column(name = "PUC_SER_VIN_CODIGO", nullable = false, precision = 3, scale = 0)
	public Short getPucSerVinCodigo() {
		return this.pucSerVinCodigo;
	}

	public void setPucSerVinCodigo(Short pucSerVinCodigo) {
		this.pucSerVinCodigo = pucSerVinCodigo;
	}

	@Column(name = "PUC_UNF_SEQ", nullable = false, precision = 4, scale = 0)
	public Short getPucUnfSeq() {
		return this.pucUnfSeq;
	}

	public void setPucUnfSeq(Short pucUnfSeq) {
		this.pucUnfSeq = pucUnfSeq;
	}

	@Column(name = "PUC_IND_FUNCAO_PROF", nullable = false, length = 3)
	@Enumerated(EnumType.STRING)
	public DominioFuncaoProfissional getPucIndFuncaoProf() {
		return this.pucIndFuncaoProf;
	}

	public void setPucIndFuncaoProf(DominioFuncaoProfissional pucIndFuncaoProf) {
		this.pucIndFuncaoProf = pucIndFuncaoProf;
	}
	

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getCrgSeq());
		umHashCodeBuilder.append(this.getPucSerMatricula());
		umHashCodeBuilder.append(this.getPucSerVinCodigo());
		umHashCodeBuilder.append(this.getPucUnfSeq());
		umHashCodeBuilder.append(this.getPucIndFuncaoProf());
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
		if (!(obj instanceof MbcProfCirurgiasId)) {
			return false;
		}
		MbcProfCirurgiasId other = (MbcProfCirurgiasId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getCrgSeq(), other.getCrgSeq());
		umEqualsBuilder.append(this.getPucSerMatricula(), other.getPucSerMatricula());
		umEqualsBuilder.append(this.getPucSerVinCodigo(), other.getPucSerVinCodigo());
		umEqualsBuilder.append(this.getPucUnfSeq(), other.getPucUnfSeq());
		umEqualsBuilder.append(this.getPucIndFuncaoProf(), other.getPucIndFuncaoProf());
		return umEqualsBuilder.isEquals();
	}
	// ##### GeradorEqualsHashCodeMain #####
	
}