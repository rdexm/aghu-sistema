package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * MamEmgServEspecialidade generated by hbm2java
 */
@Entity
@Table(name = "MAM_EMG_SERV_ESPECIALIDADES", schema = "AGH")
public class MamEmgServEspecialidade extends BaseEntityId<MamEmgServEspecialidadeId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2381188658378065540L;
	private MamEmgServEspecialidadeId id;
	private Integer version;
	private RapServidores rapServidores;
	private MamEmgServidor mamEmgServidor;
	private MamEmgEspecialidades mamEmgEspecialidades;
	private Date criadoEm;
	private String indSituacao;
	private String indProjetoEmei;
	private Set<MamEmgServEspCoop> mamEmgServEspCoopes = new HashSet<MamEmgServEspCoop>(0);

	public MamEmgServEspecialidade() {
	}

	public MamEmgServEspecialidade(MamEmgServEspecialidadeId id, RapServidores rapServidores, MamEmgServidor mamEmgServidor,
			MamEmgEspecialidades mamEmgEspecialidades, Date criadoEm, String indSituacao) {
		this.id = id;
		this.rapServidores = rapServidores;
		this.mamEmgServidor = mamEmgServidor;
		this.mamEmgEspecialidades = mamEmgEspecialidades;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
	}

	public MamEmgServEspecialidade(MamEmgServEspecialidadeId id, RapServidores rapServidores, MamEmgServidor mamEmgServidor,
			MamEmgEspecialidades mamEmgEspecialidades, Date criadoEm, String indSituacao, String indProjetoEmei,
			Set<MamEmgServEspCoop> mamEmgServEspCoopes) {
		this.id = id;
		this.rapServidores = rapServidores;
		this.mamEmgServidor = mamEmgServidor;
		this.mamEmgEspecialidades = mamEmgEspecialidades;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
		this.indProjetoEmei = indProjetoEmei;
		this.mamEmgServEspCoopes = mamEmgServEspCoopes;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "eseSeq", column = @Column(name = "ESE_SEQ", nullable = false)),
			@AttributeOverride(name = "eepEspSeq", column = @Column(name = "EEP_ESP_SEQ", nullable = false)) })
	@NotNull
	public MamEmgServEspecialidadeId getId() {
		return this.id;
	}

	public void setId(MamEmgServEspecialidadeId id) {
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
	@JoinColumn(name = "ESE_SEQ", nullable = false, insertable = false, updatable = false)
	@NotNull
	public MamEmgServidor getMamEmgServidor() {
		return this.mamEmgServidor;
	}

	public void setMamEmgServidor(MamEmgServidor mamEmgServidor) {
		this.mamEmgServidor = mamEmgServidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EEP_ESP_SEQ", nullable = false, insertable = false, updatable = false)
	@NotNull
	public MamEmgEspecialidades getMamEmgEspecialidades() {
		return this.mamEmgEspecialidades;
	}

	public void setMamEmgEspecialidades(MamEmgEspecialidades mamEmgEspecialidades) {
		this.mamEmgEspecialidades = mamEmgEspecialidades;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	@NotNull
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@NotNull
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "IND_PROJETO_EMEI", length = 1)
	@Length(max = 1)
	public String getIndProjetoEmei() {
		return this.indProjetoEmei;
	}

	public void setIndProjetoEmei(String indProjetoEmei) {
		this.indProjetoEmei = indProjetoEmei;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mamEmgServEspecialidade")
	public Set<MamEmgServEspCoop> getMamEmgServEspCoopes() {
		return this.mamEmgServEspCoopes;
	}

	public void setMamEmgServEspCoopes(Set<MamEmgServEspCoop> mamEmgServEspCoopes) {
		this.mamEmgServEspCoopes = mamEmgServEspCoopes;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		MAM_EMG_SERVIDORES("mamEmgServidor"),
		MAM_EMG_ESPECIALIDADES("mamEmgEspecialidades"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
		IND_PROJETO_EMEI("indProjetoEmei"),
		MAM_EMG_SERV_ESP_COOPES("mamEmgServEspCoopes"),
		ID_ESE_SEQ("id.eseSeq"),
		ID_EEP_ESP_SEQ("id.eepEspSeq")
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
		if (!(obj instanceof MamEmgServEspecialidade)) {
			return false;
		}
		MamEmgServEspecialidade other = (MamEmgServEspecialidade) obj;
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
