package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "MAM_TRG_GERAIS", schema = "AGH")
public class MamTrgGerais extends BaseEntityId<MamTrgGeralId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1406543638620404452L;
	private MamTrgGeralId id;
	private MamTriagens mamTriagens;
	private String complemento;
	private String micNome;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Integer version;
	private MamItemGeral mamItemGeral;
	private Boolean indUso;
	private Boolean indConsistenciaOk;
	private Date dtHrInformada;
	private Date dtHrConsistenciaOk;

	public MamTrgGerais() {
	}

	public MamTrgGerais(MamTrgGeralId id, MamTriagens mamTriagens,
			String complemento, String micNome, Date criadoEm, Integer serMatricula,
			Short serVinCodigo, MamItemGeral mamItemGeral, Boolean indUso, Boolean indConsistenciaOk,
			Date dtHrInformada, Date dtHrConsistenciaOk) {
		this.id = id;
		this.mamTriagens = mamTriagens;
		this.complemento = complemento;
		this.micNome = micNome;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.mamItemGeral = mamItemGeral;
		this.indUso = indUso;
		this.indConsistenciaOk = indConsistenciaOk;
		this.dtHrInformada = dtHrInformada;
		this.dtHrConsistenciaOk = dtHrConsistenciaOk;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "trgSeq", column = @Column(name = "TRG_SEQ", nullable = false, precision = 14, scale = 0)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 4, scale = 0)) })
	@NotNull
	public MamTrgGeralId getId() {
		return this.id;
	}

	public void setId(MamTrgGeralId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRG_SEQ", insertable = false, updatable = false)
	public MamTriagens getMamTriagens() {
		return this.mamTriagens;
	}

	public void setMamTriagens(MamTriagens mamTriagens) {
		this.mamTriagens = mamTriagens;
	}

	@Column(name = "COMPLEMENTO", nullable = true, length = 2000)
	@Length(max = 2000)
	public String getComplemento() {
		return this.complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	@Column(name = "MIC_NOME", length = 50)
	@Length(max = 50)
	public String getMicNome() {
		return this.micNome;
	}

	public void setMicNome(String micNome) {
		this.micNome = micNome;
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

	@Column(name = "SER_MATRICULA", nullable = false, precision = 7, scale = 0)
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false, precision = 3, scale = 0)
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
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
	@JoinColumn(name = "ITG_SEQ", nullable = false)
	@NotNull
	public MamItemGeral getMamItemGeral() {
		return mamItemGeral;
	}

	public void setMamItemGeral(MamItemGeral mamItemGeral) {
		this.mamItemGeral = mamItemGeral;
	}

	@Column(name = "IND_USO", nullable = false, length = 1)
	@NotNull
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndUso() {
		return indUso;
	}

	public void setIndUso(Boolean indUso) {
		this.indUso = indUso;
	}

	@Column(name = "IND_CONSISTENCIA_OK", nullable = false, length = 1)
	@NotNull
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndConsistenciaOk() {
		return indConsistenciaOk;
	}

	public void setIndConsistenciaOk(Boolean indConsistenciaOk) {
		this.indConsistenciaOk = indConsistenciaOk;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_INFORMADA", nullable = false, length = 7)
	@NotNull
	public Date getDtHrInformada() {
		return dtHrInformada;
	}

	public void setDtHrInformada(Date dtHrInformada) {
		this.dtHrInformada = dtHrInformada;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_CONSISTENCIA_OK", length = 7)
	public Date getDtHrConsistenciaOk() {
		return dtHrConsistenciaOk;
	}

	public void setDtHrConsistenciaOk(Date dtHrConsistenciaOk) {
		this.dtHrConsistenciaOk = dtHrConsistenciaOk;
	}

	public enum Fields {
		TRG_SEQ("id.trgSeq"),
		SEQP("id.seqp"),
		COMPLEMENTO("complemento"),
		MAM_ITEM_GERAL("mamItemGeral"),
		ITG_SEQ("mamItemGeral.seq");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
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
		if (!(obj instanceof MamTrgGerais)) {
			return false;
		}
		MamTrgGerais other = (MamTrgGerais) obj;
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
