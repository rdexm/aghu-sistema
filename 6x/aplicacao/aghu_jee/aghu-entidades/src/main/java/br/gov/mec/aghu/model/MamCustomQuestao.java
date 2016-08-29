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


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * ================================================================================
 *   ####   #####    ####   ######  #####   ##  ##   ####    ####    ####    #### 
 *  ##  ##  ##  ##  ##      ##      ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##
 *  ##  ##  #####    ####   ####    #####   ##  ##  ######  ##      ######  ##  ##
 *  ##  ##  ##  ##      ##  ##      ##  ##   ####   ##  ##  ##  ##  ##  ##  ##  ##
 *   ####   #####    ####   ######  ##  ##    ##    ##  ##   ####   ##  ##   #### 
 * ================================================================================
 *
 * A partir de uma análise originada pela tarefa #19993
 * esta model foi escolhida para ser apenas de leitura
 * no AGHU e por isso foi anotada como Immutable.
 *
 * Entretanto, caso esta entidade seja necessária na construção
 * de uma estória que necessite escrever dados no banco, este
 * comentário e esta anotação pode ser retirada desta model.
 */
@Immutable

@Entity
@Table(name = "MAM_CUSTOM_QUESTOES", schema = "AGH")
public class MamCustomQuestao extends BaseEntityId<MamCustomQuestaoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8788886070017808209L;
	private MamCustomQuestaoId id;
	private Integer version;
	private MamQuestao mamQuestao;
	private RapServidores rapServidores;
	private MamQuestionario mamQuestionario;
	private String indSituacao;
	private Date criadoEm;

	public MamCustomQuestao() {
	}

	public MamCustomQuestao(MamCustomQuestaoId id, MamQuestao mamQuestao, RapServidores rapServidores,
			MamQuestionario mamQuestionario, String indSituacao, Date criadoEm) {
		this.id = id;
		this.mamQuestao = mamQuestao;
		this.rapServidores = rapServidores;
		this.mamQuestionario = mamQuestionario;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "qusQutSeq", column = @Column(name = "QUS_QUT_SEQ", nullable = false)),
			@AttributeOverride(name = "qusSeqp", column = @Column(name = "QUS_SEQP", nullable = false)),
			@AttributeOverride(name = "qutSeq", column = @Column(name = "QUT_SEQ", nullable = false)) })
	public MamCustomQuestaoId getId() {
		return this.id;
	}

	public void setId(MamCustomQuestaoId id) {
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
	@JoinColumns({
			@JoinColumn(name = "QUS_QUT_SEQ", referencedColumnName = "QUT_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "QUS_SEQP", referencedColumnName = "SEQP", nullable = false, insertable = false, updatable = false) })
	public MamQuestao getMamQuestao() {
		return this.mamQuestao;
	}

	public void setMamQuestao(MamQuestao mamQuestao) {
		this.mamQuestao = mamQuestao;
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
	@JoinColumn(name = "QUT_SEQ", nullable = false, insertable = false, updatable = false)
	public MamQuestionario getMamQuestionario() {
		return this.mamQuestionario;
	}

	public void setMamQuestionario(MamQuestionario mamQuestionario) {
		this.mamQuestionario = mamQuestionario;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
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
		QUS_QUT_SEQ("id.qusQutSeq"),
		QUS_SEQP("id.qusSeqp"),
		VERSION("version"),
		MAM_QUESTOES("mamQuestao"),
		RAP_SERVIDORES("rapServidores"),
		MAM_QUESTIONARIOS("mamQuestionario"),
		IND_SITUACAO("indSituacao"),
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
		if (!(obj instanceof MamCustomQuestao)) {
			return false;
		}
		MamCustomQuestao other = (MamCustomQuestao) obj;
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
