package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

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
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * MptExtratoHrGradeSessao generated by hbm2java
 */
@Entity
@Table(name = "MPT_EXTRATO_HR_GRADE_SESSOES", schema = "AGH")
public class MptExtratoHrGradeSessao extends BaseEntityId<MptExtratoHrGradeSessaoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7156356183169694389L;
	private MptExtratoHrGradeSessaoId id;
	private Integer version;
	private MptAgendaSessao mptAgendaSessao;
	private RapServidores rapServidores;
	private MptHorarioGradeSessao mptHorarioGradeSessao;
	private String tipoMovimento;

	public MptExtratoHrGradeSessao() {
	}

	public MptExtratoHrGradeSessao(MptExtratoHrGradeSessaoId id, RapServidores rapServidores,
			MptHorarioGradeSessao mptHorarioGradeSessao, String tipoMovimento) {
		this.id = id;
		this.rapServidores = rapServidores;
		this.mptHorarioGradeSessao = mptHorarioGradeSessao;
		this.tipoMovimento = tipoMovimento;
	}

	public MptExtratoHrGradeSessao(MptExtratoHrGradeSessaoId id, MptAgendaSessao mptAgendaSessao, RapServidores rapServidores,
			MptHorarioGradeSessao mptHorarioGradeSessao, String tipoMovimento) {
		this.id = id;
		this.mptAgendaSessao = mptAgendaSessao;
		this.rapServidores = rapServidores;
		this.mptHorarioGradeSessao = mptHorarioGradeSessao;
		this.tipoMovimento = tipoMovimento;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "criadoEm", column = @Column(name = "CRIADO_EM", nullable = false, length = 29)),
			@AttributeOverride(name = "hgsGseSeq", column = @Column(name = "HGS_GSE_SEQ", nullable = false)),
			@AttributeOverride(name = "hgsSeqp", column = @Column(name = "HGS_SEQP", nullable = false)) })
	public MptExtratoHrGradeSessaoId getId() {
		return this.id;
	}

	public void setId(MptExtratoHrGradeSessaoId id) {
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
	@JoinColumn(name = "AGE_SEQ")
	public MptAgendaSessao getMptAgendaSessao() {
		return this.mptAgendaSessao;
	}

	public void setMptAgendaSessao(MptAgendaSessao mptAgendaSessao) {
		this.mptAgendaSessao = mptAgendaSessao;
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
	@JoinColumns({
			@JoinColumn(name = "HGS_GSE_SEQ", referencedColumnName = "GSE_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "HGS_SEQP", referencedColumnName = "SEQP", nullable = false, insertable = false, updatable = false) })
	public MptHorarioGradeSessao getMptHorarioGradeSessao() {
		return this.mptHorarioGradeSessao;
	}

	public void setMptHorarioGradeSessao(MptHorarioGradeSessao mptHorarioGradeSessao) {
		this.mptHorarioGradeSessao = mptHorarioGradeSessao;
	}

	@Column(name = "TIPO_MOVIMENTO", nullable = false, length = 1)
	@Length(max = 1)
	public String getTipoMovimento() {
		return this.tipoMovimento;
	}

	public void setTipoMovimento(String tipoMovimento) {
		this.tipoMovimento = tipoMovimento;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		MPT_AGENDA_SESSOES("mptAgendaSessao"),
		RAP_SERVIDORES("rapServidores"),
		MPT_HORARIO_GRADE_SESSOES("mptHorarioGradeSessao"),
		TIPO_MOVIMENTO("tipoMovimento");

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
		if (!(obj instanceof MptExtratoHrGradeSessao)) {
			return false;
		}
		MptExtratoHrGradeSessao other = (MptExtratoHrGradeSessao) obj;
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
