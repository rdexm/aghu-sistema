package br.gov.mec.aghu.model;

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
import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "MAM_RESP_QUEST_EVOLUCOES", schema = "AGH")
public class MamRespQuestEvolucoes extends BaseEntityId<MamRespQuestEvolucoesId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2324531288109533507L;
	private MamRespQuestEvolucoesId id;
	private MamRespostaEvolucoes respostaEvolucao;

	public MamRespQuestEvolucoes() {
	}

	public MamRespQuestEvolucoes(MamRespQuestEvolucoesId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "revEvoSeq", column = @Column(name = "REV_EVO_SEQ", nullable = false, precision = 14, scale = 0)),
			@AttributeOverride(name = "revQusQutSeq", column = @Column(name = "REV_QUS_QUT_SEQ", nullable = false, precision = 6, scale = 0)),
			@AttributeOverride(name = "revQusSeqp", column = @Column(name = "REV_QUS_SEQP", nullable = false, precision = 3, scale = 0)),
			@AttributeOverride(name = "revSeqp", column = @Column(name = "REV_SEQP", nullable = false, precision = 3, scale = 0)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 6, scale = 0))})
	public MamRespQuestEvolucoesId getId() {
		return this.id;
	}

	public void setId(MamRespQuestEvolucoesId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "REV_EVO_SEQ", referencedColumnName = "EVO_SEQ",  insertable = false, updatable = false),
        @JoinColumn(name = "REV_QUS_QUT_SEQ", referencedColumnName = "QUS_QUT_SEQ", insertable=false, updatable=false),
        @JoinColumn(name = "REV_QUS_SEQP", referencedColumnName = "QUS_SEQP", insertable=false, updatable=false),
        @JoinColumn(name = "REV_SEQP", referencedColumnName = "SEQP", insertable=false, updatable=false)})
	
	public MamRespostaEvolucoes getRespostaEvolucao() {
		return respostaEvolucao;
	}

	public void setRespostaEvolucao(MamRespostaEvolucoes respostaEvolucao) {
		this.respostaEvolucao = respostaEvolucao;
	}
	
	public enum Fields {
		REV_EVO_SEQ("id.revEvoSeq"),
		REV_QUS_QUT_SEQ("id.revQusQutSeq"),
		REV_QUS_SEQP("id.revQusSeqp"),
		RESPOSTA_EVOLUCAO("respostaEvolucao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
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
		if (!(obj instanceof MamRespQuestEvolucoes)) {
			return false;
		}
		MamRespQuestEvolucoes other = (MamRespQuestEvolucoes) obj;
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