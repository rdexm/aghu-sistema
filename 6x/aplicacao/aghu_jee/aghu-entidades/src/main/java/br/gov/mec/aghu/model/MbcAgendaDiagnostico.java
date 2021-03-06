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
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;


import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * MbcAgendaDiagnostico generated by hbm2java
 */
@Entity
@Table(name = "MBC_AGENDA_DIAGNOSTICOS", schema = "AGH", uniqueConstraints = @UniqueConstraint(columnNames = "agd_seq"))
public class MbcAgendaDiagnostico extends BaseEntityId<MbcAgendaDiagnosticoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5438361977449317542L;
	private MbcAgendaDiagnosticoId id;
	private Integer version;
	private AghCid aghCid;
	private MbcAgendas mbcAgendas;
	private RapServidores rapServidores;
	private Date criadoEm;

	public MbcAgendaDiagnostico() {
	}

	public MbcAgendaDiagnostico(MbcAgendaDiagnosticoId id, AghCid aghCid, MbcAgendas mbcAgendas, RapServidores rapServidores,
			Date criadoEm) {
		this.id = id;
		this.aghCid = aghCid;
		this.mbcAgendas = mbcAgendas;
		this.rapServidores = rapServidores;
		this.criadoEm = criadoEm;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "agdSeq", column = @Column(name = "AGD_SEQ", unique = true, nullable = false)),
			@AttributeOverride(name = "cidSeq", column = @Column(name = "CID_SEQ", nullable = false)) })
	public MbcAgendaDiagnosticoId getId() {
		return this.id;
	}

	public void setId(MbcAgendaDiagnosticoId id) {
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
	@JoinColumn(name = "CID_SEQ", nullable = false, insertable = false, updatable = false)
	public AghCid getAghCid() {
		return this.aghCid;
	}

	public void setAghCid(AghCid aghCid) {
		this.aghCid = aghCid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AGD_SEQ", unique = true, nullable = false, insertable = false, updatable = false)
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

	public enum Fields {

		ID("id"),
		VERSION("version"),
		AGH_CID("aghCid"),
		MBC_AGENDAS("mbcAgendas"),
		RAP_SERVIDORES("rapServidores"),
		CRIADO_EM("criadoEm"),
		AGH_SEQ("aghCid.seq"),;

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
		if (!(obj instanceof MbcAgendaDiagnostico)) {
			return false;
		}
		MbcAgendaDiagnostico other = (MbcAgendaDiagnostico) obj;
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
