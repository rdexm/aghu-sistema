package br.gov.mec.aghu.model;

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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "MAM_UNID_X_GERAIS", schema = "AGH")
public class MamUnidXGeral extends BaseEntityId<MamUnidXGeralId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5653359530069371056L;
	private MamUnidXGeralId id;
	private Integer version;
	private MamUnidAtendem mamUnidAtendem;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Boolean indObrigatorio;
	private Date criadoEm;
	private DominioSituacao indSituacao;
	private MamItemGeral mamItemGeral;

	public MamUnidXGeral() {
	}

	public MamUnidXGeral(MamUnidXGeralId id, MamUnidAtendem mamUnidAtendem,
			Short serVinCodigo, Integer serMatricula, Boolean indObrigatorio,
			Date criadoEm, DominioSituacao indSituacao) {
		this.id = id;
		this.mamUnidAtendem = mamUnidAtendem;
		this.serVinCodigo = serVinCodigo;
		this.serMatricula = serMatricula;
		this.indObrigatorio = indObrigatorio;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "itgSeq", column = @Column(name = "ITG_SEQ", nullable = false)),
			@AttributeOverride(name = "uanUnfSeq", column = @Column(name = "UAN_UNF_SEQ", nullable = false)) })
	@NotNull
	public MamUnidXGeralId getId() {
		return this.id;
	}

	public void setId(MamUnidXGeralId id) {
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
	@JoinColumn(name = "UAN_UNF_SEQ", nullable = false, insertable = false, updatable = false)
	@NotNull
	public MamUnidAtendem getMamUnidAtendem() {
		return this.mamUnidAtendem;
	}

	public void setMamUnidAtendem(MamUnidAtendem mamUnidAtendem) {
		this.mamUnidAtendem = mamUnidAtendem;
	}

	@Column(name = "SER_MATRICULA")
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO")
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}
	
	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
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
	
	@Column(name = "IND_SITUACAO", nullable = false, length=1)
	@NotNull
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ITG_SEQ", nullable = false, insertable = false, updatable = false)
	@NotNull
	public MamItemGeral getMamItemGeral() {
		return mamItemGeral;
	}

	public void setMamItemGeral(MamItemGeral mamItemGeral) {
		this.mamItemGeral = mamItemGeral;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		MAM_UNID_ATENDEM("mamUnidAtendem"),
		UAN_UNF_SEQ("mamUnidAtendem.unfSeq"),
		RAP_SERVIDORES("rapServidores"),
		IND_OBRIGATORIO("indObrigatorio"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
		MAM_ITEM_GERAL("mamItemGeral"),
		ITG_SEQ("mamItemGeral.seq");

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
		if (!(obj instanceof MamUnidXGeral)) {
			return false;
		}
		MamUnidXGeral other = (MamUnidXGeral) obj;
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
