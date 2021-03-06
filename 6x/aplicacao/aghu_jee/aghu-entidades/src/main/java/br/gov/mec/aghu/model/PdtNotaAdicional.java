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
import javax.persistence.Version;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * PdtNotaAdicional generated by hbm2java
 */
@Entity
@Table(name = "PDT_NOTA_ADICIONAIS", schema = "AGH")
public class PdtNotaAdicional extends BaseEntityId<PdtNotaAdicionalId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1494526502833586013L;
	private PdtNotaAdicionalId id;
	private Integer version;
	private PdtDescricao pdtDescricao;
	private RapServidores rapServidores;
	private String notaAdicional;
	private Date criadoEm;

	public PdtNotaAdicional() {
	}

	public PdtNotaAdicional(PdtNotaAdicionalId id, PdtDescricao pdtDescricao, RapServidores rapServidores, String notaAdicional,
			Date criadoEm) {
		this.id = id;
		this.pdtDescricao = pdtDescricao;
		this.rapServidores = rapServidores;
		this.notaAdicional = notaAdicional;
		this.criadoEm = criadoEm;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "ddtSeq", column = @Column(name = "DDT_SEQ", nullable = false)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false)) })
	public PdtNotaAdicionalId getId() {
		return this.id;
	}

	public void setId(PdtNotaAdicionalId id) {
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
	@JoinColumn(name = "DDT_SEQ", nullable = false, insertable = false, updatable = false)
	public PdtDescricao getPdtDescricao() {
		return this.pdtDescricao;
	}

	public void setPdtDescricao(PdtDescricao pdtDescricao) {
		this.pdtDescricao = pdtDescricao;
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

	@Column(name = "NOTA_ADICIONAL", nullable = false, length = 4000)
	public String getNotaAdicional() {
		return this.notaAdicional;
	}

	public void setNotaAdicional(String notaAdicional) {
		this.notaAdicional = notaAdicional;
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
		DDT_SEQ("id.ddtSeq"),
		SEQP("id.seqp"),
		VERSION("version"),
		PDT_DESCRICAO("pdtDescricao"),
		RAP_SERVIDORES("rapServidores"),
		NOTA_ADICIONAL("notaAdicional"),
		CRIADO_EM("criadoEm");

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
		if (!(obj instanceof PdtNotaAdicional)) {
			return false;
		}
		PdtNotaAdicional other = (PdtNotaAdicional) obj;
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
