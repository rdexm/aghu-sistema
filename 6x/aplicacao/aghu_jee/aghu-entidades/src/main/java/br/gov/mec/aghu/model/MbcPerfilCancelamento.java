package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "MBC_PERFIL_CANCELAMENTOS", schema = "AGH")
public class MbcPerfilCancelamento extends BaseEntityId<MbcPerfilCancelamentoId> implements java.io.Serializable {

	private static final long serialVersionUID = 3760316812163120903L;
	private MbcPerfilCancelamentoId id;
	private Integer version;
	private RapServidores rapServidores;
	private MbcMotivoCancelamento mbcMotivoCancelamento;
	private Date criadoEm;
	private Perfil perfil;

	public MbcPerfilCancelamento() {
	}

	public MbcPerfilCancelamento(MbcPerfilCancelamentoId id, RapServidores rapServidores,
			MbcMotivoCancelamento mbcMotivoCancelamento, Date criadoEm) {
		this.id = id;
		this.rapServidores = rapServidores;
		this.mbcMotivoCancelamento = mbcMotivoCancelamento;
		this.criadoEm = criadoEm;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "mtcSeq", column = @Column(name = "MTC_SEQ", nullable = false)),
			@AttributeOverride(name = "perNome", column = @Column(name = "PER_NOME", nullable = false, length = 30)) })
	public MbcPerfilCancelamentoId getId() {
		return this.id;
	}

	public void setId(MbcPerfilCancelamentoId id) {
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
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MTC_SEQ", nullable = false, insertable = false, updatable = false)
	public MbcMotivoCancelamento getMbcMotivoCancelamento() {
		return this.mbcMotivoCancelamento;
	}

	public void setMbcMotivoCancelamento(MbcMotivoCancelamento mbcMotivoCancelamento) {
		this.mbcMotivoCancelamento = mbcMotivoCancelamento;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PER_NOME", referencedColumnName = "NOME", nullable = false, insertable = false, updatable = false)
	public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}
	
	
	public enum Fields {

		ID("id"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		MBC_MOTIVO_CANCELAMENTO("mbcMotivoCancelamento"),
		CRIADO_EM("criadoEm"),
		MTC_SEQ("id.mtcSeq"),
		PER_NOME("id.perNome")
		;

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
		if (!(obj instanceof MbcPerfilCancelamento)) {
			return false;
		}
		MbcPerfilCancelamento other = (MbcPerfilCancelamento) obj;
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
