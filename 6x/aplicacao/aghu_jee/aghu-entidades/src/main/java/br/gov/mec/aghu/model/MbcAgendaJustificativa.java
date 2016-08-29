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


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioTipoAgendaJustificativa;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * MbcAgendaJustificativa generated by hbm2java
 */
@Entity
@Table(name = "MBC_AGENDA_JUSTIFICATIVAS", schema = "AGH")
public class MbcAgendaJustificativa extends BaseEntityId<MbcAgendaJustificativaId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7242576407899092192L;
	private MbcAgendaJustificativaId id;
	private Integer version;
	private MbcAgendas mbcAgendas;
	private RapServidores rapServidores;
	private Date criadoEm;
	private DominioTipoAgendaJustificativa tipo;
	private String justificativa;

	public MbcAgendaJustificativa() {
	}

	public MbcAgendaJustificativa(MbcAgendaJustificativaId id, MbcAgendas mbcAgendas, RapServidores rapServidores, Date criadoEm,
			DominioTipoAgendaJustificativa tipo, String justificativa) {
		this.id = id;
		this.mbcAgendas = mbcAgendas;
		this.rapServidores = rapServidores;
		this.criadoEm = criadoEm;
		this.tipo = tipo;
		this.justificativa = justificativa;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "agdSeq", column = @Column(name = "AGD_SEQ", nullable = false)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false)) })
	public MbcAgendaJustificativaId getId() {
		return this.id;
	}

	public void setId(MbcAgendaJustificativaId id) {
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
	@JoinColumn(name = "AGD_SEQ", nullable = false, insertable = false, updatable = false)
	public MbcAgendas getMbcAgendas() {
		return this.mbcAgendas;
	}

	public void setMbcAgendas(MbcAgendas mbcAgendas) {
		this.mbcAgendas = mbcAgendas;
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "TIPO", nullable = false, length = 3)
	@Enumerated(EnumType.STRING)
	public DominioTipoAgendaJustificativa getTipo() {
		return this.tipo;
	}

	public void setTipo(DominioTipoAgendaJustificativa tipo) {
		this.tipo = tipo;
	}

	@Column(name = "JUSTIFICATIVA", nullable = false, length = 500)
	@Length(max = 500)
	public String getJustificativa() {
		return this.justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public enum Fields {

		ID("id"),
		ID_AGD_SEQ("id.agdSeq"),
		ID_SEQP("id.seqp"),
		VERSION("version"),
		MBC_AGENDAS("mbcAgendas"),
		RAP_SERVIDORES("rapServidores"),
		CRIADO_EM("criadoEm"),
		TIPO("tipo"),
		JUSTIFICATIVA("justificativa");

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
		if (!(obj instanceof MbcAgendaJustificativa)) {
			return false;
		}
		MbcAgendaJustificativa other = (MbcAgendaJustificativa) obj;
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