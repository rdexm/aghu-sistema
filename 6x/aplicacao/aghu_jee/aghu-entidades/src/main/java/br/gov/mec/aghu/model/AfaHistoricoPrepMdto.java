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


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * AfaHistoricoPrepMdto generated by hbm2java
 */
@Entity
@Table(name = "AFA_HISTORICO_PREP_MDTOS", schema = "AGH")
public class AfaHistoricoPrepMdto extends BaseEntityId<AfaHistoricoPrepMdtoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8470406401641968378L;
	private AfaHistoricoPrepMdtoId id;
	private Integer version;
	private RapServidores rapServidores;
	private AfaItemPreparoMdto afaItemPreparoMdto;
	private AfaPreparoMdto afaPreparoMdto;
	private Date criadoEm;
	private String origem;
	private String operacao;
	private String descricao;
	private String indSitPreparo;

	public AfaHistoricoPrepMdto() {
	}

	public AfaHistoricoPrepMdto(AfaHistoricoPrepMdtoId id, RapServidores rapServidores, AfaPreparoMdto afaPreparoMdto,
			Date criadoEm, String origem, String operacao, String descricao) {
		this.id = id;
		this.rapServidores = rapServidores;
		this.afaPreparoMdto = afaPreparoMdto;
		this.criadoEm = criadoEm;
		this.origem = origem;
		this.operacao = operacao;
		this.descricao = descricao;
	}

	public AfaHistoricoPrepMdto(AfaHistoricoPrepMdtoId id, RapServidores rapServidores, AfaItemPreparoMdto afaItemPreparoMdto,
			AfaPreparoMdto afaPreparoMdto, Date criadoEm, String origem, String operacao, String descricao, String indSitPreparo) {
		this.id = id;
		this.rapServidores = rapServidores;
		this.afaItemPreparoMdto = afaItemPreparoMdto;
		this.afaPreparoMdto = afaPreparoMdto;
		this.criadoEm = criadoEm;
		this.origem = origem;
		this.operacao = operacao;
		this.descricao = descricao;
		this.indSitPreparo = indSitPreparo;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "ptoSeq", column = @Column(name = "PTO_SEQ", nullable = false)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 8, scale = 8)) })
	public AfaHistoricoPrepMdtoId getId() {
		return this.id;
	}

	public void setId(AfaHistoricoPrepMdtoId id) {
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
	@JoinColumns({ @JoinColumn(name = "ITO_SEQP", referencedColumnName = "SEQP"),
			@JoinColumn(name = "ITO_PTO_SEQ", referencedColumnName = "PTO_SEQ") })
	public AfaItemPreparoMdto getAfaItemPreparoMdto() {
		return this.afaItemPreparoMdto;
	}

	public void setAfaItemPreparoMdto(AfaItemPreparoMdto afaItemPreparoMdto) {
		this.afaItemPreparoMdto = afaItemPreparoMdto;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PTO_SEQ", nullable = false, insertable = false, updatable = false)
	public AfaPreparoMdto getAfaPreparoMdto() {
		return this.afaPreparoMdto;
	}

	public void setAfaPreparoMdto(AfaPreparoMdto afaPreparoMdto) {
		this.afaPreparoMdto = afaPreparoMdto;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "ORIGEM", nullable = false, length = 1)
	@Length(max = 1)
	public String getOrigem() {
		return this.origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	@Column(name = "OPERACAO", nullable = false, length = 1)
	@Length(max = 1)
	public String getOperacao() {
		return this.operacao;
	}

	public void setOperacao(String operacao) {
		this.operacao = operacao;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 2000)
	@Length(max = 2000)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_SIT_PREPARO", length = 1)
	@Length(max = 1)
	public String getIndSitPreparo() {
		return this.indSitPreparo;
	}

	public void setIndSitPreparo(String indSitPreparo) {
		this.indSitPreparo = indSitPreparo;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		AFA_ITEM_PREPARO_MDTOS("afaItemPreparoMdto"),
		AFA_PREPARO_MDTOS("afaPreparoMdto"),
		CRIADO_EM("criadoEm"),
		ORIGEM("origem"),
		OPERACAO("operacao"),
		DESCRICAO("descricao"),
		IND_SIT_PREPARO("indSitPreparo");

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
		if (!(obj instanceof AfaHistoricoPrepMdto)) {
			return false;
		}
		AfaHistoricoPrepMdto other = (AfaHistoricoPrepMdto) obj;
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
