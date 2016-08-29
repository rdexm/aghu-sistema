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
@Table(name = "MAM_UNID_QUEST_GRP_PATOLS", schema = "AGH")
public class MamUnidQuestGrpPatol extends BaseEntityId<MamUnidQuestGrpPatolId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -830260691656044927L;
	private MamUnidQuestGrpPatolId id;
	private Integer version;
	private MamUnidQuestionario mamUnidQuestionario;
	private RapServidores rapServidores;
	private MamEmgGrpPatologia mamEmgGrpPatologia;
	private Date criadoEm;
	private String indSituacao;

	public MamUnidQuestGrpPatol() {
	}

	public MamUnidQuestGrpPatol(MamUnidQuestGrpPatolId id, MamUnidQuestionario mamUnidQuestionario, RapServidores rapServidores,
			MamEmgGrpPatologia mamEmgGrpPatologia, Date criadoEm, String indSituacao) {
		this.id = id;
		this.mamUnidQuestionario = mamUnidQuestionario;
		this.rapServidores = rapServidores;
		this.mamEmgGrpPatologia = mamEmgGrpPatologia;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "unqUnfSeq", column = @Column(name = "UNQ_UNF_SEQ", nullable = false)),
			@AttributeOverride(name = "unqQutSeq", column = @Column(name = "UNQ_QUT_SEQ", nullable = false)),
			@AttributeOverride(name = "eplSeq", column = @Column(name = "EPL_SEQ", nullable = false)) })
	public MamUnidQuestGrpPatolId getId() {
		return this.id;
	}

	public void setId(MamUnidQuestGrpPatolId id) {
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
			@JoinColumn(name = "UNQ_UNF_SEQ", referencedColumnName = "UNF_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "UNQ_QUT_SEQ", referencedColumnName = "QUT_SEQ", nullable = false, insertable = false, updatable = false) })
	public MamUnidQuestionario getMamUnidQuestionario() {
		return this.mamUnidQuestionario;
	}

	public void setMamUnidQuestionario(MamUnidQuestionario mamUnidQuestionario) {
		this.mamUnidQuestionario = mamUnidQuestionario;
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
	@JoinColumn(name = "EPL_SEQ", nullable = false, insertable = false, updatable = false)
	public MamEmgGrpPatologia getMamEmgGrpPatologia() {
		return this.mamEmgGrpPatologia;
	}

	public void setMamEmgGrpPatologia(MamEmgGrpPatologia mamEmgGrpPatologia) {
		this.mamEmgGrpPatologia = mamEmgGrpPatologia;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		MAM_UNID_QUESTIONARIOS("mamUnidQuestionario"),
		RAP_SERVIDORES("rapServidores"),
		MAM_EMG_GRP_PATOLOGIAS("mamEmgGrpPatologia"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao");

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
		if (!(obj instanceof MamUnidQuestGrpPatol)) {
			return false;
		}
		MamUnidQuestGrpPatol other = (MamUnidQuestGrpPatol) obj;
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
