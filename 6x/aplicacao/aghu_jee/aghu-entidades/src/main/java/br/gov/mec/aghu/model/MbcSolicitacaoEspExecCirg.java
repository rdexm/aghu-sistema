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


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "MBC_SOLICITACAO_ESP_EXEC_CIRGS", schema = "AGH")
public class MbcSolicitacaoEspExecCirg extends BaseEntityId<MbcSolicitacaoEspExecCirgId> implements java.io.Serializable {

	private static final long serialVersionUID = 8594993529446777512L;
	private MbcSolicitacaoEspExecCirgId id;
	private Integer version;
	private MbcCirurgias mbcCirurgias;
	private RapServidores rapServidores;
	private MbcNecessidadeCirurgica mbcNecessidadeCirurgica;
	private Date criadoEm;
	private String descricao;

	public MbcSolicitacaoEspExecCirg() {
	}

	public MbcSolicitacaoEspExecCirg(MbcSolicitacaoEspExecCirgId id, MbcCirurgias mbcCirurgias, RapServidores rapServidores,
			MbcNecessidadeCirurgica mbcNecessidadeCirurgica, Date criadoEm) {
		this.id = id;
		this.mbcCirurgias = mbcCirurgias;
		this.rapServidores = rapServidores;
		this.mbcNecessidadeCirurgica = mbcNecessidadeCirurgica;
		this.criadoEm = criadoEm;
	}

	public MbcSolicitacaoEspExecCirg(MbcSolicitacaoEspExecCirgId id, MbcCirurgias mbcCirurgias, RapServidores rapServidores,
			MbcNecessidadeCirurgica mbcNecessidadeCirurgica, Date criadoEm, String descricao) {
		this.id = id;
		this.mbcCirurgias = mbcCirurgias;
		this.rapServidores = rapServidores;
		this.mbcNecessidadeCirurgica = mbcNecessidadeCirurgica;
		this.criadoEm = criadoEm;
		this.descricao = descricao;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "crgSeq", column = @Column(name = "CRG_SEQ", nullable = false)),
			@AttributeOverride(name = "nciSeq", column = @Column(name = "NCI_SEQ", nullable = false)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false)) })
	public MbcSolicitacaoEspExecCirgId getId() {
		return this.id;
	}

	public void setId(MbcSolicitacaoEspExecCirgId id) {
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
	@JoinColumn(name = "CRG_SEQ", nullable = false, insertable = false, updatable = false)
	public MbcCirurgias getMbcCirurgias() {
		return this.mbcCirurgias;
	}

	public void setMbcCirurgias(MbcCirurgias mbcCirurgias) {
		this.mbcCirurgias = mbcCirurgias;
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
	@JoinColumn(name = "NCI_SEQ", nullable = false, insertable = false, updatable = false)
	public MbcNecessidadeCirurgica getMbcNecessidadeCirurgica() {
		return this.mbcNecessidadeCirurgica;
	}

	public void setMbcNecessidadeCirurgica(MbcNecessidadeCirurgica mbcNecessidadeCirurgica) {
		this.mbcNecessidadeCirurgica = mbcNecessidadeCirurgica;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "DESCRICAO", length = 120)
	@Length(max = 120)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public enum Fields {

		ID("id"),
		ID_CRG_SEQ("id.crgSeq"),
		ID_NCI_SEQ("id.nciSeq"),
		ID_SEQP("id.seqp"),
		VERSION("version"),
		MBC_CIRURGIAS("mbcCirurgias"),
		RAP_SERVIDORES("rapServidores"),
		MBC_NECESSIDADE_CIRURGICAS("mbcNecessidadeCirurgica"),
		CRIADO_EM("criadoEm"),
		DESCRICAO("descricao");

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
		if (!(obj instanceof MbcSolicitacaoEspExecCirg)) {
			return false;
		}
		MbcSolicitacaoEspExecCirg other = (MbcSolicitacaoEspExecCirg) obj;
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

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id",this.id).toString();
	}

}
