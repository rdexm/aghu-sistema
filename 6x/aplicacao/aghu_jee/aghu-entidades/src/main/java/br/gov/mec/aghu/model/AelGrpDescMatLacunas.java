package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * The persistent class for the AEL_GRP_DESC_MAT_LACUNAS database table.
 * 
 */
@Entity
@Table(name = "AEL_GRP_DESC_MAT_LACUNAS", schema = "AGH")
public class AelGrpDescMatLacunas extends BaseEntityId<AelGrpDescMatLacunasId> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9123978945094614953L;
	private AelGrpDescMatLacunasId id;
	private AelTxtDescMats aelTxtDescMats;
	private String lacuna;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private RapServidores servidor;
	private Integer version;
	private Set<AelDescMatLacunas> aelDescMatLacunas = new HashSet<AelDescMatLacunas>(0);

	public AelGrpDescMatLacunas() {
	}

	public AelGrpDescMatLacunas(AelGrpDescMatLacunasId id, AelTxtDescMats aelTxtDescMats, String lacuna, DominioSituacao indSituacao,
			Date criadoEm, Integer serMatricula, Short serVinCodigo) {
		this.id = id;
		this.aelTxtDescMats = aelTxtDescMats;
		this.lacuna = lacuna;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
	}

	public AelGrpDescMatLacunas(AelGrpDescMatLacunasId id, AelTxtDescMats aelTxtDescMats, String lacuna, DominioSituacao indSituacao,
			Date criadoEm, Set<AelDescMatLacunas> aelDescMatLacunas) {
		this.id = id;
		this.aelTxtDescMats = aelTxtDescMats;
		this.lacuna = lacuna;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.aelDescMatLacunas = aelDescMatLacunas;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "gtmSeq", column = @Column(name = "GTM_SEQ", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "ldaSeq", column = @Column(name = "LDA_SEQ", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 4, scale = 0)) })
	@NotNull
	public AelGrpDescMatLacunasId getId() {
		return this.id;
	}

	public void setId(AelGrpDescMatLacunasId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "GTM_SEQ", referencedColumnName = "GTM_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "LDA_SEQ", referencedColumnName = "SEQP", nullable = false, insertable = false, updatable = false) })
	@NotNull
	public AelTxtDescMats getAelTxtDescMats() {
		return this.aelTxtDescMats;
	}

	public void setAelTxtDescMats(AelTxtDescMats aelTxtDescMats) {
		this.aelTxtDescMats = aelTxtDescMats;
	}

	@Column(name = "LACUNA", nullable = false, length = 10)
	@NotNull
	@Length(max = 10)
	public String getLacuna() {
		return this.lacuna;
	}

	public void setLacuna(String lacuna) {
		this.lacuna = lacuna;
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
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	@NotNull
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
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

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aelGrpDescMatLacunas")
	public Set<AelDescMatLacunas> getAelDescMatLacunass() {
		return this.aelDescMatLacunas;
	}

	public void setAelDescMatLacunass(Set<AelDescMatLacunas> aelDescMatLacunas) {
		this.aelDescMatLacunas = aelDescMatLacunas;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		AelGrpDescMatLacunas other = (AelGrpDescMatLacunas) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	public enum Fields {
		GTM_SEQ("id.gtmSeq"), LDA_SEQ("id.ldaSeq"), SEQP("id.seqp"), LACUNA("lacuna"), IND_SITUACAO("indSituacao"), CRIADO_EM(
				"criadoEm"), AEL_TXT_DESC_MATS_GTM_SEQ("aelTxtDescMats.id.gtmSeq"), AEL_TXT_DESC_MATS_SEQP(
				"aelTxtDescMats.id.seqp"), AEL_TXT_DESC_MATS("aelTxtDescMats"), ;

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

}
