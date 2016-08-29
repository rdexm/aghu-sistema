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

@Entity
@Table(name = "MAM_UNID_X_EXAMES", schema = "AGH")
public class MamUnidXExame extends BaseEntityId<MamUnidXExameId> implements java.io.Serializable {

	private static final long serialVersionUID = -5653359530069371056L;
	private MamUnidXExameId id;
	private Integer version;
	private MamItemExame mamItemExame;
	private MamUnidAtendem mamUnidAtendem;
	private RapServidores rapServidores;
	private Boolean indObrigatorio;
	private Date criadoEm;
	private DominioSituacao indSituacao;
	private String micNome;
	private Set<MamExameUnidGrav> mamExameUnidGraves = new HashSet<MamExameUnidGrav>(0);

	public MamUnidXExame() {
	}

	public MamUnidXExame(MamUnidXExameId id, MamItemExame mamItemExame, MamUnidAtendem mamUnidAtendem,
			RapServidores rapServidores, Boolean indObrigatorio, Date criadoEm, DominioSituacao indSituacao) {
		this.id = id;
		this.mamItemExame = mamItemExame;
		this.mamUnidAtendem = mamUnidAtendem;
		this.rapServidores = rapServidores;
		this.indObrigatorio = indObrigatorio;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
	}

	public MamUnidXExame(MamUnidXExameId id, MamItemExame mamItemExame, MamUnidAtendem mamUnidAtendem,
			RapServidores rapServidores, Boolean indObrigatorio, Date criadoEm, DominioSituacao indSituacao, String micNome,
			Set<MamExameUnidGrav> mamExameUnidGraves) {
		this.id = id;
		this.mamItemExame = mamItemExame;
		this.mamUnidAtendem = mamUnidAtendem;
		this.rapServidores = rapServidores;
		this.indObrigatorio = indObrigatorio;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
		this.micNome = micNome;
		this.mamExameUnidGraves = mamExameUnidGraves;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "emsSeq", column = @Column(name = "EMS_SEQ", nullable = false)),
			@AttributeOverride(name = "uanUnfSeq", column = @Column(name = "UAN_UNF_SEQ", nullable = false)) })
	@NotNull
	public MamUnidXExameId getId() {
		return this.id;
	}

	public void setId(MamUnidXExameId id) {
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
	@JoinColumn(name = "EMS_SEQ", nullable = false, insertable = false, updatable = false)
	@NotNull
	public MamItemExame getMamItemExame() {
		return this.mamItemExame;
	}

	public void setMamItemExame(MamItemExame mamItemExame) {
		this.mamItemExame = mamItemExame;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UAN_UNF_SEQ", nullable = false, insertable = false, updatable = false)
	@NotNull
	public MamUnidAtendem getMamUnidAtendem() {
		return this.mamUnidAtendem;
	}

	public void setMamUnidAtendem(MamUnidAtendem mamUnidAtendem) {
		this.mamUnidAtendem = mamUnidAtendem;
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

	@Column(name = "IND_OBRIGATORIO", nullable = false, length = 1)
	@NotNull
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndObrigatorio() {
		return this.indObrigatorio;
	}

	public void setIndObrigatorio(Boolean indObrigatorio) {
		this.indObrigatorio = indObrigatorio;
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
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "MIC_NOME", length = 50)
	@Length(max = 50)
	public String getMicNome() {
		return this.micNome;
	}

	public void setMicNome(String micNome) {
		this.micNome = micNome;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mamUnidXExame")
	public Set<MamExameUnidGrav> getMamExameUnidGraves() {
		return this.mamExameUnidGraves;
	}

	public void setMamExameUnidGraves(Set<MamExameUnidGrav> mamExameUnidGraves) {
		this.mamExameUnidGraves = mamExameUnidGraves;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		MAM_ITEM_EXAME("mamItemExame"),
		EMS_SEQ("mamItemExame.seq"),
		MAM_UNID_ATENDEM("mamUnidAtendem"),
		UAN_UNF_SEQ("mamUnidAtendem.unfSeq"),
		RAP_SERVIDORES("rapServidores"),
		IND_OBRIGATORIO("indObrigatorio"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
		MIC_NOME("micNome"),
		MAM_EXAME_UNID_GRAVES("mamExameUnidGraves");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}


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
		if (!(obj instanceof MamUnidXExame)) {
			return false;
		}
		MamUnidXExame other = (MamUnidXExame) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
}
