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
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacaoRegistro;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * MamExtratoRegistro generated by hbm2java
 */
@Entity
@Table(name = "MAM_EXTRATO_REGISTROS", schema = "AGH")
public class MamExtratoRegistro extends BaseEntityId<MamExtratoRegistroId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4406778800912940542L;
	private MamExtratoRegistroId id;
	private Integer version;
	private DominioSituacaoRegistro indSituacao;
	private Date criadoEm;
	private String micNome;
//	private Integer serMatricula;
//	private Short serVinCodigo;
	private RapServidores servidor;

	public MamExtratoRegistro() {
	}

	public MamExtratoRegistro(MamExtratoRegistroId id, DominioSituacaoRegistro indSituacao, Date criadoEm, RapServidores servidor) {
		this.id = id;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
//		this.serMatricula = serMatricula;
//		this.serVinCodigo = serVinCodigo;
	}

	public MamExtratoRegistro(MamExtratoRegistroId id, DominioSituacaoRegistro indSituacao, Date criadoEm, String micNome, RapServidores servidor) {
		this.id = id;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.micNome = micNome;
		this.servidor = servidor;
//		this.serMatricula = serMatricula;
//		this.serVinCodigo = serVinCodigo;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "rgtSeq", column = @Column(name = "RGT_SEQ", nullable = false)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false)) })
	@NotNull
	public MamExtratoRegistroId getId() {
		return this.id;
	}

	public void setId(MamExtratoRegistroId id) {
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

	@Column(name = "IND_SITUACAO", nullable = false, length = 2)
	@Enumerated(EnumType.STRING)
	@NotNull
	public DominioSituacaoRegistro getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacaoRegistro indSituacao) {
		this.indSituacao = indSituacao;
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

//	@Column(name = "SER_MATRICULA", nullable = false)
//	public Integer getSerMatricula() {
//		return this.serMatricula;
//	}
//
//	public void setSerMatricula(Integer serMatricula) {
//		this.serMatricula = serMatricula;
//	}
//
//	@Column(name = "SER_VIN_CODIGO", nullable = false)
//	public Short getSerVinCodigo() {
//		return this.serVinCodigo;
//	}
//
//	public void setSerVinCodigo(Short serVinCodigo) {
//		this.serVinCodigo = serVinCodigo;
//	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public enum Fields {

		ID("id"),
		ID_RGT_SEQ("id.rgtSeq"),
		ID_SEQP("id.seqp"),
		VERSION("version"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		MIC_NOME("micNome"),
		SERVIDOR("servidor");
//		SER_MATRICULA("serMatricula"),
//		SER_VIN_CODIGO("serVinCodigo");

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
		if (!(obj instanceof MamExtratoRegistro)) {
			return false;
		}
		MamExtratoRegistro other = (MamExtratoRegistro) obj;
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
