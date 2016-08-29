package br.gov.mec.aghu.model;

import java.io.Serializable;

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
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "MAM_RESP_QUEST_ANAMNESES", schema = "AGH")
public class MamRespQuestAnamneses extends BaseEntityId<MamRespQuestAnamnesesId> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4131454179067637154L;
	
	private MamRespQuestAnamnesesId id;
	private Integer rvqQusQutSeq;
	private Short rvqQusSeqp;
	private Integer rvqSeqp;
	private Integer version;
	
	private MamRespostaAnamneses respostaAnamnese;
	private MamRespValidaQuestoes respValidaQuestoes;

	public MamRespQuestAnamneses() {
	}

	public MamRespQuestAnamneses(MamRespQuestAnamnesesId id) {
		this.id = id;
	}

	public MamRespQuestAnamneses(MamRespQuestAnamnesesId id,
			Integer rvqQusQutSeq, Short rvqQusSeqp, Integer rvqSeqp) {
		this.id = id;
		this.rvqQusQutSeq = rvqQusQutSeq;
		this.rvqQusSeqp = rvqQusSeqp;
		this.rvqSeqp = rvqSeqp;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "reaAnaSeq", column = @Column(name = "REA_ANA_SEQ", nullable = false, precision = 14, scale = 0)),
			@AttributeOverride(name = "reaQusQutSeq", column = @Column(name = "REA_QUS_QUT_SEQ", nullable = false, precision = 6, scale = 0)),
			@AttributeOverride(name = "reaQusSeqp", column = @Column(name = "REA_QUS_SEQP", nullable = false, precision = 3, scale = 0)),
			@AttributeOverride(name = "reaSeqp", column = @Column(name = "REA_SEQP", nullable = false, precision = 3, scale = 0)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 6, scale = 0)) })
	public MamRespQuestAnamnesesId getId() {
		return this.id;
	}

	public void setId(MamRespQuestAnamnesesId id) {
		this.id = id;
	}

	@Column(name = "RVQ_QUS_QUT_SEQ", nullable = false, precision = 6, scale = 0)
	public Integer getRvqQusQutSeq() {
		return rvqQusQutSeq;
	}

	public void setRvqQusQutSeq(Integer rvqQusQutSeq) {
		this.rvqQusQutSeq = rvqQusQutSeq;
	}

	@Column(name = "RVQ_QUS_SEQP", nullable = false, precision = 3, scale = 0)
	public Short getRvqQusSeqp() {
		return rvqQusSeqp;
	}

	public void setRvqQusSeqp(Short rvqQusSeqp) {
		this.rvqQusSeqp = rvqQusSeqp;
	}

	@Column(name = "RVQ_SEQP", nullable = false, precision = 5, scale = 0)
	public Integer getRvqSeqp() {
		return rvqSeqp;
	}

	public void setRvqSeqp(Integer rvqSeqp) {
		this.rvqSeqp = rvqSeqp;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "REA_ANA_SEQ", referencedColumnName = "ANA_SEQ", insertable = false, updatable = false),
		@JoinColumn(name = "REA_QUS_QUT_SEQ", referencedColumnName = "QUS_QUT_SEQ", insertable = false, updatable = false),
		@JoinColumn(name = "REA_QUS_SEQP", referencedColumnName = "QUS_SEQP", insertable = false, updatable = false),
		@JoinColumn(name = "REA_SEQP", referencedColumnName = "SEQP", insertable = false, updatable = false) })
	public MamRespostaAnamneses getRespostaAnamnese() {
		return respostaAnamnese;
	}
	
	public void setRespostaAnamnese(MamRespostaAnamneses respostaAnamnese) {
		this.respostaAnamnese = respostaAnamnese;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "RVQ_QUS_QUT_SEQ", referencedColumnName = "QUS_QUT_SEQ", insertable = false, updatable = false),
		@JoinColumn(name = "RVQ_QUS_SEQP", referencedColumnName = "QUS_SEQP", insertable = false, updatable = false),
		@JoinColumn(name = "RVQ_SEQP", referencedColumnName = "SEQP", insertable = false, updatable = false) })
	public MamRespValidaQuestoes getRespValidaQuestoes() {
		return respValidaQuestoes;
	}

	public void setRespValidaQuestoes(MamRespValidaQuestoes respValidaQuestoes) {
		this.respValidaQuestoes = respValidaQuestoes;
	}
	
	public enum Fields {
		REA_ANA_SEQ("id.reaAnaSeq"),
		REA_QUS_QUT_SEQ("id.reaQusQutSeq"),
		REA_QUS_SEQP("id.reaQusSeqp"),
		REA_SEQP("id.reaSeqp"),
		SEQP("id.seqp"),
		RVQ_QUS_QUT_SEQ("rvqQusQutSeq"),
		RVQ_QUS_SEQP("rvqQusSeqp"),
		RVQ_SEQP("rvqSeqp");
		
		private String fields;
		
		private Fields(String fields) {
			this.fields = fields;
		}
		
		@Override
		public String toString() {
			return fields;
		}
	}

	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
        umHashCodeBuilder.append(this.getId());
        umHashCodeBuilder.append(this.getRvqQusQutSeq());
        umHashCodeBuilder.append(this.getRvqQusSeqp());
        umHashCodeBuilder.append(this.getRvqSeqp());
        return umHashCodeBuilder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MamRespQuestAnamneses other = (MamRespQuestAnamneses) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
        umEqualsBuilder.append(this.getId(), other.getId());
        umEqualsBuilder.append(this.getRvqQusQutSeq(), other.getRvqQusQutSeq());
        umEqualsBuilder.append(this.getRvqQusSeqp(), other.getRvqQusSeqp());
        umEqualsBuilder.append(this.getRvqSeqp(), other.getRvqSeqp());
        return umEqualsBuilder.isEquals();
	}
}
