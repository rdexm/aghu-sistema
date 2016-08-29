package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

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
@Table(name = "CCE_AMOSTRA_OBSERVACOES", schema = "AGH")
public class CceAmostraObservacao extends BaseEntityId<CceAmostraObservacaoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4380331493771856686L;
	private CceAmostraObservacaoId id;
	private Integer version;
	private CceAmostraContagem cceAmostraContagem;
	private CceObservacao cceObservacao;
	private String descricaoLivre;

	public CceAmostraObservacao() {
	}

	public CceAmostraObservacao(CceAmostraObservacaoId id, CceAmostraContagem cceAmostraContagem, CceObservacao cceObservacao) {
		this.id = id;
		this.cceAmostraContagem = cceAmostraContagem;
		this.cceObservacao = cceObservacao;
	}

	public CceAmostraObservacao(CceAmostraObservacaoId id, CceAmostraContagem cceAmostraContagem, CceObservacao cceObservacao,
			String descricaoLivre) {
		this.id = id;
		this.cceAmostraContagem = cceAmostraContagem;
		this.cceObservacao = cceObservacao;
		this.descricaoLivre = descricaoLivre;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "camNumero", column = @Column(name = "CAM_NUMERO", nullable = false)),
			@AttributeOverride(name = "camSeqp", column = @Column(name = "CAM_SEQP", nullable = false)),
			@AttributeOverride(name = "obsSeq", column = @Column(name = "OBS_SEQ", nullable = false)) })
	public CceAmostraObservacaoId getId() {
		return this.id;
	}

	public void setId(CceAmostraObservacaoId id) {
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
			@JoinColumn(name = "CAM_NUMERO", referencedColumnName = "NUMERO", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "CAM_SEQP", referencedColumnName = "SEQP", nullable = false, insertable = false, updatable = false) })
	public CceAmostraContagem getCceAmostraContagem() {
		return this.cceAmostraContagem;
	}

	public void setCceAmostraContagem(CceAmostraContagem cceAmostraContagem) {
		this.cceAmostraContagem = cceAmostraContagem;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OBS_SEQ", nullable = false, insertable = false, updatable = false)
	public CceObservacao getCceObservacao() {
		return this.cceObservacao;
	}

	public void setCceObservacao(CceObservacao cceObservacao) {
		this.cceObservacao = cceObservacao;
	}

	@Column(name = "DESCRICAO_LIVRE", length = 2000)
	@Length(max = 2000)
	public String getDescricaoLivre() {
		return this.descricaoLivre;
	}

	public void setDescricaoLivre(String descricaoLivre) {
		this.descricaoLivre = descricaoLivre;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		CCE_AMOSTRAS_CONTAGEM("cceAmostraContagem"),
		CCE_OBSERVACOES("cceObservacao"),
		DESCRICAO_LIVRE("descricaoLivre");

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
		if (!(obj instanceof CceAmostraObservacao)) {
			return false;
		}
		CceAmostraObservacao other = (CceAmostraObservacao) obj;
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
