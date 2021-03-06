package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * AfaDecimalComponenteNpt generated by hbm2java
 */
@Entity
@Table(name = "AFA_DECIMAL_COMPONENTE_NPTS", schema = "AGH")
public class AfaDecimalComponenteNpt extends BaseEntityId<AfaDecimalComponenteNptId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 192129093621019267L;
	private AfaDecimalComponenteNptId id;
	private Integer version;
	private Short nroCasasDecimais;
	private Double pesoInicial;
	private Double pesoFinal;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Date alteradoEm;
	private Integer serMatriculaAlteradoPor;
	private Short serVinCodigoAlteradoPor;

	public AfaDecimalComponenteNpt() {
	}

	public AfaDecimalComponenteNpt(AfaDecimalComponenteNptId id, Short nroCasasDecimais, Double pesoInicial, Double pesoFinal,
			Date criadoEm, Integer serMatricula, Short serVinCodigo) {
		this.id = id;
		this.nroCasasDecimais = nroCasasDecimais;
		this.pesoInicial = pesoInicial;
		this.pesoFinal = pesoFinal;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
	}

	public AfaDecimalComponenteNpt(AfaDecimalComponenteNptId id, Short nroCasasDecimais, Double pesoInicial, Double pesoFinal,
			Date criadoEm, Integer serMatricula, Short serVinCodigo, Date alteradoEm, Integer serMatriculaAlteradoPor,
			Short serVinCodigoAlteradoPor) {
		this.id = id;
		this.nroCasasDecimais = nroCasasDecimais;
		this.pesoInicial = pesoInicial;
		this.pesoFinal = pesoFinal;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.alteradoEm = alteradoEm;
		this.serMatriculaAlteradoPor = serMatriculaAlteradoPor;
		this.serVinCodigoAlteradoPor = serVinCodigoAlteradoPor;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "cnpMedMatCodigo", column = @Column(name = "CNP_MED_MAT_CODIGO", nullable = false)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false)) })
	public AfaDecimalComponenteNptId getId() {
		return this.id;
	}

	public void setId(AfaDecimalComponenteNptId id) {
		this.id = id;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "NRO_CASAS_DECIMAIS", nullable = false)
	public Short getNroCasasDecimais() {
		return this.nroCasasDecimais;
	}

	public void setNroCasasDecimais(Short nroCasasDecimais) {
		this.nroCasasDecimais = nroCasasDecimais;
	}

	@Column(name = "PESO_INICIAL", nullable = false, precision = 17, scale = 17)
	public Double getPesoInicial() {
		return this.pesoInicial;
	}

	public void setPesoInicial(Double pesoInicial) {
		this.pesoInicial = pesoInicial;
	}

	@Column(name = "PESO_FINAL", nullable = false, precision = 17, scale = 17)
	public Double getPesoFinal() {
		return this.pesoFinal;
	}

	public void setPesoFinal(Double pesoFinal) {
		this.pesoFinal = pesoFinal;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "SER_MATRICULA", nullable = false)
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false)
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 29)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@Column(name = "SER_MATRICULA_ALTERADO_POR")
	public Integer getSerMatriculaAlteradoPor() {
		return this.serMatriculaAlteradoPor;
	}

	public void setSerMatriculaAlteradoPor(Integer serMatriculaAlteradoPor) {
		this.serMatriculaAlteradoPor = serMatriculaAlteradoPor;
	}

	@Column(name = "SER_VIN_CODIGO_ALTERADO_POR")
	public Short getSerVinCodigoAlteradoPor() {
		return this.serVinCodigoAlteradoPor;
	}

	public void setSerVinCodigoAlteradoPor(Short serVinCodigoAlteradoPor) {
		this.serVinCodigoAlteradoPor = serVinCodigoAlteradoPor;
	}

	public enum Fields {

		ID("id"),
		CNP_MED_MAT_CODIGO("id.cnpMedMatCodigo"),
		SEQP("id.seqp"),
		VERSION("version"),
		NRO_CASAS_DECIMAIS("nroCasasDecimais"),
		PESO_INICIAL("pesoInicial"),
		PESO_FINAL("pesoFinal"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		ALTERADO_EM("alteradoEm"),
		SER_MATRICULA_ALTERADO_POR("serMatriculaAlteradoPor"),
		SER_VIN_CODIGO_ALTERADO_POR("serVinCodigoAlteradoPor");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

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
		if (!(obj instanceof AfaDecimalComponenteNpt)) {
			return false;
		}
		AfaDecimalComponenteNpt other = (AfaDecimalComponenteNpt) obj;
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
