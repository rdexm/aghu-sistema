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
 * AbsExameMetodoConfirm generated by hbm2java
 */
@Entity
@Table(name = "ABS_EXAME_METODO_CONFIRMS", schema = "AGH")
public class AbsExameMetodoConfirm extends BaseEntityId<AbsExameMetodoConfirmId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 387734058760446359L;
	private AbsExameMetodoConfirmId id;
	private Integer version;
	private AbsExameMetodos absExameMetodosByAbsEmnEmmFk2;
	private RapServidores rapServidores;
	private AbsExameMetodos absExameMetodosByAbsEmnEmmFk1;
	private Date criadoEm;

	public AbsExameMetodoConfirm() {
	}

	public AbsExameMetodoConfirm(AbsExameMetodoConfirmId id, AbsExameMetodos absExameMetodosByAbsEmnEmmFk2,
			RapServidores rapServidores, AbsExameMetodos absExameMetodosByAbsEmnEmmFk1, Date criadoEm) {
		this.id = id;
		this.absExameMetodosByAbsEmnEmmFk2 = absExameMetodosByAbsEmnEmmFk2;
		this.rapServidores = rapServidores;
		this.absExameMetodosByAbsEmnEmmFk1 = absExameMetodosByAbsEmnEmmFk1;
		this.criadoEm = criadoEm;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "emmMetSeq", column = @Column(name = "EMM_MET_SEQ", nullable = false)),
			@AttributeOverride(name = "emmUfeEmaExaSigla", column = @Column(name = "EMM_UFE_EMA_EXA_SIGLA", nullable = false, length = 5)),
			@AttributeOverride(name = "emmUfeEmaManSeq", column = @Column(name = "EMM_UFE_EMA_MAN_SEQ", nullable = false)),
			@AttributeOverride(name = "emmUfeUnfSeq", column = @Column(name = "EMM_UFE_UNF_SEQ", nullable = false)),
			@AttributeOverride(name = "emmMetSeqFilho", column = @Column(name = "EMM_MET_SEQ_FILHO", nullable = false)),
			@AttributeOverride(name = "emmUfeEmaExaSiglaFilho", column = @Column(name = "EMM_UFE_EMA_EXA_SIGLA_FILHO", nullable = false, length = 5)),
			@AttributeOverride(name = "emmUfeEmaManSeqFilho", column = @Column(name = "EMM_UFE_EMA_MAN_SEQ_FILHO", nullable = false)),
			@AttributeOverride(name = "emmUfeUnfSeqFilho", column = @Column(name = "EMM_UFE_UNF_SEQ_FILHO", nullable = false)) })
	public AbsExameMetodoConfirmId getId() {
		return this.id;
	}

	public void setId(AbsExameMetodoConfirmId id) {
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
			@JoinColumn(name = "EMM_MET_SEQ_FILHO", referencedColumnName = "MET_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "EMM_UFE_EMA_EXA_SIGLA_FILHO", referencedColumnName = "UFE_EMA_EXA_SIGLA", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "EMM_UFE_EMA_MAN_SEQ_FILHO", referencedColumnName = "UFE_EMA_MAN_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "EMM_UFE_UNF_SEQ_FILHO", referencedColumnName = "UFE_UNF_SEQ", nullable = false, insertable = false, updatable = false) })
	public AbsExameMetodos getAbsExameMetodosByAbsEmnEmmFk2() {
		return this.absExameMetodosByAbsEmnEmmFk2;
	}

	public void setAbsExameMetodosByAbsEmnEmmFk2(AbsExameMetodos absExameMetodosByAbsEmnEmmFk2) {
		this.absExameMetodosByAbsEmnEmmFk2 = absExameMetodosByAbsEmnEmmFk2;
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
	@JoinColumns({
			@JoinColumn(name = "EMM_MET_SEQ", referencedColumnName = "MET_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "EMM_UFE_EMA_EXA_SIGLA", referencedColumnName = "UFE_EMA_EXA_SIGLA", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "EMM_UFE_EMA_MAN_SEQ", referencedColumnName = "UFE_EMA_MAN_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "EMM_UFE_UNF_SEQ", referencedColumnName = "UFE_UNF_SEQ", nullable = false, insertable = false, updatable = false) })
	public AbsExameMetodos getAbsExameMetodosByAbsEmnEmmFk1() {
		return this.absExameMetodosByAbsEmnEmmFk1;
	}

	public void setAbsExameMetodosByAbsEmnEmmFk1(AbsExameMetodos absExameMetodosByAbsEmnEmmFk1) {
		this.absExameMetodosByAbsEmnEmmFk1 = absExameMetodosByAbsEmnEmmFk1;
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
		VERSION("version"),
		ABS_EXAME_METODOS_BY_ABS_EMN_EMM_FK2("absExameMetodosByAbsEmnEmmFk2"),
		RAP_SERVIDORES("rapServidores"),
		ABS_EXAME_METODOS_BY_ABS_EMN_EMM_FK1("absExameMetodosByAbsEmnEmmFk1"),
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
		if (!(obj instanceof AbsExameMetodoConfirm)) {
			return false;
		}
		AbsExameMetodoConfirm other = (AbsExameMetodoConfirm) obj;
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