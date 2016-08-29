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
@Table(name = "MCI_GRUPO_PROCED_RISCOS", schema = "AGH")
public class MciGrupoProcedRisco extends BaseEntityId<MciGrupoProcedRiscoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8489529485694850712L;
	private MciGrupoProcedRiscoId id;
	private Integer version;
	private RapServidores rapServidoresByMciGrsSerFk2;
	private MciProcedimentoRisco mciProcedimentoRisco;
	private MciTipoGrupoProcedRisco mciTipoGrupoProcedRisco;
	private RapServidores rapServidoresByMciGrsSerFk1;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private Date alteradoEm;

	public MciGrupoProcedRisco() {
	}

	public MciGrupoProcedRisco(MciGrupoProcedRiscoId id, RapServidores rapServidoresByMciGrsSerFk2,
			MciProcedimentoRisco mciProcedimentoRisco, MciTipoGrupoProcedRisco mciTipoGrupoProcedRisco, DominioSituacao indSituacao,
			Date criadoEm) {
		this.id = id;
		this.rapServidoresByMciGrsSerFk2 = rapServidoresByMciGrsSerFk2;
		this.mciProcedimentoRisco = mciProcedimentoRisco;
		this.mciTipoGrupoProcedRisco = mciTipoGrupoProcedRisco;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
	}

	public MciGrupoProcedRisco(MciGrupoProcedRiscoId id, RapServidores rapServidoresByMciGrsSerFk2,
			MciProcedimentoRisco mciProcedimentoRisco, MciTipoGrupoProcedRisco mciTipoGrupoProcedRisco,
			RapServidores rapServidoresByMciGrsSerFk1, DominioSituacao indSituacao, Date criadoEm, Date alteradoEm) {
		this.id = id;
		this.rapServidoresByMciGrsSerFk2 = rapServidoresByMciGrsSerFk2;
		this.mciProcedimentoRisco = mciProcedimentoRisco;
		this.mciTipoGrupoProcedRisco = mciTipoGrupoProcedRisco;
		this.rapServidoresByMciGrsSerFk1 = rapServidoresByMciGrsSerFk1;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.alteradoEm = alteradoEm;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "porSeq", column = @Column(name = "POR_SEQ", nullable = false)),
			@AttributeOverride(name = "tgpSeq", column = @Column(name = "TGP_SEQ", nullable = false)) })
	public MciGrupoProcedRiscoId getId() {
		return this.id;
	}

	public void setId(MciGrupoProcedRiscoId id) {
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
	public RapServidores getRapServidoresByMciGrsSerFk2() {
		return this.rapServidoresByMciGrsSerFk2;
	}

	public void setRapServidoresByMciGrsSerFk2(RapServidores rapServidoresByMciGrsSerFk2) {
		this.rapServidoresByMciGrsSerFk2 = rapServidoresByMciGrsSerFk2;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "POR_SEQ", nullable = false, insertable = false, updatable = false)
	public MciProcedimentoRisco getMciProcedimentoRisco() {
		return this.mciProcedimentoRisco;
	}

	public void setMciProcedimentoRisco(MciProcedimentoRisco mciProcedimentoRisco) {
		this.mciProcedimentoRisco = mciProcedimentoRisco;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TGP_SEQ", nullable = false, insertable = false, updatable = false)
	public MciTipoGrupoProcedRisco getMciTipoGrupoProcedRisco() {
		return this.mciTipoGrupoProcedRisco;
	}

	public void setMciTipoGrupoProcedRisco(MciTipoGrupoProcedRisco mciTipoGrupoProcedRisco) {
		this.mciTipoGrupoProcedRisco = mciTipoGrupoProcedRisco;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_MOVIMENTADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_MOVIMENTADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByMciGrsSerFk1() {
		return this.rapServidoresByMciGrsSerFk1;
	}

	public void setRapServidoresByMciGrsSerFk1(RapServidores rapServidoresByMciGrsSerFk1) {
		this.rapServidoresByMciGrsSerFk1 = rapServidoresByMciGrsSerFk1;
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
		POR_SEQ("id.porSeq"),
		TGP_SEQ("id.tgpSeq"),
		VERSION("version"),
		SERVIDOR("rapServidoresByMciGrsSerFk2"),
		RAP_SERVIDORES_BY_MCI_GRS_SER_FK2("rapServidoresByMciGrsSerFk2"),
		MCI_PROCEDIMENTO_RISCOS("mciProcedimentoRisco"),
		MCI_TIPO_GRUPO_PROCED_RISCOS("mciTipoGrupoProcedRisco"),
		MCI_TIPO_GRUPO_PROCED_RISCOS_SEQ("mciTipoGrupoProcedRisco.seq"),
		RAP_SERVIDORES_BY_MCI_GRS_SER_FK1("rapServidoresByMciGrsSerFk1"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		ALTERADO_EM("alteradoEm"),
		SER_MATRICULA("rapServidoresByMciGrsSerFk2.id.matricula"),
		SER_VIN_CODIGO("rapServidoresByMciGrsSerFk2.id.vinCodigo");

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
		if (!(obj instanceof MciGrupoProcedRisco)) {
			return false;
		}
		MciGrupoProcedRisco other = (MciGrupoProcedRisco) obj;
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
