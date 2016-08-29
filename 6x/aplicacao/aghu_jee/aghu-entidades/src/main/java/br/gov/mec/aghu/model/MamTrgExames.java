package br.gov.mec.aghu.model;

// Generated 14/10/2011 11:28:27 by Hibernate Tools 3.4.0.CR1

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

@SuppressWarnings({"PMD.AghuUsoIndevidoDaEnumDominioSimNaoEntity"})
@Entity
@Table(name = "MAM_TRG_EXAMES", schema = "AGH")
@org.hibernate.annotations.Cache(usage = org.hibernate.annotations.CacheConcurrencyStrategy.TRANSACTIONAL)
public class MamTrgExames extends BaseEntityId<MamTrgExamesId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1793918104308173691L;
	private MamTrgExamesId id;
	private MamTriagens mamTriagens;
	private Integer version;
	private String complemento;
	private Date dthrInformada;
	private Date dthrConsistenciaOk;
	private Boolean indUso;
	private Boolean indConsistenciaOk;
	private Date criadoEm;
	private String micNome;
	private Integer serMatricula;
	private Short serVinCodigo;

	private MamItemExame itemExame;
	
	public MamTrgExames() {
	}

	public MamTrgExames(MamTrgExamesId id, Date dthrInformada, Boolean indUso,
			Boolean indConsistenciaOk, Date criadoEm,
			Integer serMatricula, Short serVinCodigo) {
		this.id = id;
		this.dthrInformada = dthrInformada;
		this.indUso = indUso;
		this.indConsistenciaOk = indConsistenciaOk;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
	}

	public MamTrgExames(MamTrgExamesId id, String complemento,
			Date dthrInformada, Date dthrConsistenciaOk, Boolean indUso,
			Boolean indConsistenciaOk, Date criadoEm,
			String micNome, Integer serMatricula, Short serVinCodigo) {
		this.id = id;
		this.complemento = complemento;
		this.dthrInformada = dthrInformada;
		this.dthrConsistenciaOk = dthrConsistenciaOk;
		this.indUso = indUso;
		this.indConsistenciaOk = indConsistenciaOk;
		this.criadoEm = criadoEm;
		this.micNome = micNome;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "trgSeq", column = @Column(name = "TRG_SEQ", nullable = false)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false)) })
	public MamTrgExamesId getId() {
		return this.id;
	}

	public void setId(MamTrgExamesId id) {
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

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "COMPLEMENTO", length = 2000)
	@Length(max = 2000)
	public String getComplemento() {
		return this.complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_INFORMADA", nullable = false, length = 29)
	@NotNull
	public Date getDthrInformada() {
		return this.dthrInformada;
	}

	public void setDthrInformada(Date dthrInformada) {
		this.dthrInformada = dthrInformada;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_CONSISTENCIA_OK", length = 29)
	public Date getDthrConsistenciaOk() {
		return this.dthrConsistenciaOk;
	}

	public void setDthrConsistenciaOk(Date dthrConsistenciaOk) {
		this.dthrConsistenciaOk = dthrConsistenciaOk;
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
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	@NotNull
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "MIC_NOME", length = 50)
	@Length(max = 50)
	public String getMicNome() {
		return this.micNome;
	}

	public void setMicNome(String micNome) {
		this.micNome = micNome;
	}

	@Column(name = "SER_MATRICULA", nullable = false)
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false)
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EMS_SEQ", referencedColumnName="SEQ", nullable = false)
	public MamItemExame getItemExame() {
		return itemExame;
	}

	public void setItemExame(MamItemExame itemExame) {
		this.itemExame = itemExame;
	}

	public enum Fields {
		TRG_SEQ("id.trgSeq"),
		SEQP("id.seqp"), 
		DTHR_CONSISTENCIA_OK("dthrConsistenciaOk"),
		EMS_SEQ("itemExame.seq"), 
		MAM_ITEM_EXAME("itemExame"),
		SER_MATRICULA("serMatricula"), 
		SER_VIN_CODIGO("serVinCodigo"),
//		ITEM_EXAME("itemExame"),
		IND_USO("indUso");
				
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
		if (!(obj instanceof MamTrgExames)) {
			return false;
		}
		MamTrgExames other = (MamTrgExames) obj;
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
