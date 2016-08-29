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


import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;


@Entity
@Table(name = "MBC_CID_USUAL_EQUIPES", schema = "AGH")
public class MbcCidUsualEquipe extends BaseEntityId<MbcCidUsualEquipeId> implements java.io.Serializable {

	private static final long serialVersionUID = -2833253683515429241L;
	private MbcCidUsualEquipeId id;
	private Integer version;
	private AghCid aghCid;
	private RapServidores rapServidores;
	private AghEquipes aghEquipes;
	private Date criadoEm;
	private DominioSituacao indSituacao;
	private Date alteradoEm;

	public MbcCidUsualEquipe() {
	}

	public MbcCidUsualEquipe(MbcCidUsualEquipeId id, AghCid aghCid, RapServidores rapServidores, AghEquipes aghEquipes,
			Date criadoEm, DominioSituacao indSituacao) {
		this.id = id;
		this.aghCid = aghCid;
		this.rapServidores = rapServidores;
		this.aghEquipes = aghEquipes;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
	}

	public MbcCidUsualEquipe(MbcCidUsualEquipeId id, AghCid aghCid, RapServidores rapServidores, AghEquipes aghEquipes,
			Date criadoEm, DominioSituacao indSituacao, Date alteradoEm) {
		this.id = id;
		this.aghCid = aghCid;
		this.rapServidores = rapServidores;
		this.aghEquipes = aghEquipes;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
		this.alteradoEm = alteradoEm;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "eqpSeq", column = @Column(name = "EQP_SEQ", nullable = false)),
			@AttributeOverride(name = "cidSeq", column = @Column(name = "CID_SEQ", nullable = false)) })
	public MbcCidUsualEquipeId getId() {
		return this.id;
	}

	public void setId(MbcCidUsualEquipeId id) {
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
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EQP_SEQ", nullable = false, insertable = false, updatable = false)
	public AghEquipes getAghEquipes() {
		return this.aghEquipes;
	}

	public void setAghEquipes(AghEquipes aghEquipes) {
		this.aghEquipes = aghEquipes;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 29)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}
	
	
	public enum Fields {

		ID("id"),
		VERSION("version"),
		AGH_CID("aghCid"),
		RAP_SERVIDORES("rapServidores"),
		AGH_EQUIPES("aghEquipes"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
		ALTERADO_EM("alteradoEm"),
		EQP_SEQ("id.eqpSeq"),
		CID_SEQ("aghCid.seq"),
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
		if (!(obj instanceof MbcCidUsualEquipe)) {
			return false;
		}
		MbcCidUsualEquipe other = (MbcCidUsualEquipe) obj;
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

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

}
