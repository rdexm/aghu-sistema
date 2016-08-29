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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import br.gov.mec.aghu.dominio.DominioTmo;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "MBC_TRANSP_MED_OSSEAS", schema = "AGH")
public class MbcTranspMedOssea extends BaseEntityId<MbcTranspMedOsseaId> implements java.io.Serializable {

	private static final long serialVersionUID = 2939022581806071802L;
	
	private MbcTranspMedOsseaId id;
	private Integer version;
	private RapServidores servidor;
	private AghUnidadesFuncionais unidadeFuncional;
	private AipPacientes paciente;
	private Date dtTransplante;
	private Date criadoEm;
	private DominioTmo indTmo;
	
	public MbcTranspMedOssea() {
	}

	public MbcTranspMedOssea(MbcTranspMedOsseaId id, RapServidores servidor, AipPacientes paciente, Date dtTransplante,
			Date criadoEm) {
		this.id = id;
		this.servidor = servidor;
		this.paciente = paciente;
		this.dtTransplante = dtTransplante;
		this.criadoEm = criadoEm;
	}

	public MbcTranspMedOssea(MbcTranspMedOsseaId id, RapServidores servidor, AghUnidadesFuncionais unidadeFuncional,
			AipPacientes paciente, Date dtTransplante, Date criadoEm, DominioTmo indTmo) {
		this.id = id;
		this.servidor = servidor;
		this.unidadeFuncional = unidadeFuncional;
		this.paciente = paciente;
		this.dtTransplante = dtTransplante;
		this.criadoEm = criadoEm;
		this.indTmo = indTmo;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "pacCodigo", column = @Column(name = "PAC_CODIGO", nullable = false)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false)) })
	public MbcTranspMedOsseaId getId() {
		return this.id;
	}

	public void setId(MbcTranspMedOsseaId id) {
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UNF_SEQ")
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return this.unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAC_CODIGO", nullable = false, insertable = false, updatable = false)
	public AipPacientes getPaciente() {
		return this.paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_TRANSPLANTE", nullable = false, length = 29)
	public Date getDtTransplante() {
		return this.dtTransplante;
	}

	public void setDtTransplante(Date dtTransplante) {
		this.dtTransplante = dtTransplante;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		SERVIDOR("servidor"),
		UNIDADES_FUNCIONAL("unidadeFuncional"),
		PACIENTE("paciente"),
		DT_TRANSPLANTE("dtTransplante"),
		CRIADO_EM("criadoEm"),
		IND_TMO("indTmo");

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
		if (!(obj instanceof MbcTranspMedOssea)) {
			return false;
		}
		MbcTranspMedOssea other = (MbcTranspMedOssea) obj;
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

	@Column(name = "IND_TMO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTmo getIndTmo() {
		return indTmo;
	}

	public void setIndTmo(DominioTmo indTmo) {
		this.indTmo = indTmo;
	}

}
