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
@Table(name = "RAR_ITENS_RESOLUCAO", schema = "AGH")
public class RarItemResolucao extends BaseEntityId<RarItemResolucaoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3345913788616302172L;
	private RarItemResolucaoId id;
	private Integer version;
	private RarResolucao rarResolucao;
	private String descricao;
	private String resumo;
	private Date dtInicio;
	private Date dtFim;

	public RarItemResolucao() {
	}

	public RarItemResolucao(RarItemResolucaoId id, RarResolucao rarResolucao, String descricao, Date dtInicio) {
		this.id = id;
		this.rarResolucao = rarResolucao;
		this.descricao = descricao;
		this.dtInicio = dtInicio;
	}

	public RarItemResolucao(RarItemResolucaoId id, RarResolucao rarResolucao, String descricao, String resumo, Date dtInicio,
			Date dtFim) {
		this.id = id;
		this.rarResolucao = rarResolucao;
		this.descricao = descricao;
		this.resumo = resumo;
		this.dtInicio = dtInicio;
		this.dtFim = dtFim;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "resSeq", column = @Column(name = "RES_SEQ", nullable = false)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false)) })
	public RarItemResolucaoId getId() {
		return this.id;
	}

	public void setId(RarItemResolucaoId id) {
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
	@JoinColumn(name = "RES_SEQ", nullable = false, insertable = false, updatable = false)
	public RarResolucao getRarResolucao() {
		return this.rarResolucao;
	}

	public void setRarResolucao(RarResolucao rarResolucao) {
		this.rarResolucao = rarResolucao;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 300)
	@Length(max = 300)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "RESUMO", length = 40)
	@Length(max = 40)
	public String getResumo() {
		return this.resumo;
	}

	public void setResumo(String resumo) {
		this.resumo = resumo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_INICIO", nullable = false, length = 29)
	public Date getDtInicio() {
		return this.dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_FIM", length = 29)
	public Date getDtFim() {
		return this.dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		RAR_RESOLUCOES("rarResolucao"),
		DESCRICAO("descricao"),
		RESUMO("resumo"),
		DT_INICIO("dtInicio"),
		DT_FIM("dtFim");

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
		if (!(obj instanceof RarItemResolucao)) {
			return false;
		}
		RarItemResolucao other = (RarItemResolucao) obj;
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
