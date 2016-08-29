package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;


import org.hibernate.annotations.Immutable;

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
@Table(name = "SGQ_ORIGENS", schema = "AGH")
public class SgqOrigem extends BaseEntityId<SgqOrigemId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4111747559858158167L;
	private SgqOrigemId id;
	private Integer version;
	private SgqQuery sgqQuery;
	private AghSistemas aghSistemas;

	public SgqOrigem() {
	}

	public SgqOrigem(SgqOrigemId id, SgqQuery sgqQuery, AghSistemas aghSistemas) {
		this.id = id;
		this.sgqQuery = sgqQuery;
		this.aghSistemas = aghSistemas;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "qrySeq", column = @Column(name = "QRY_SEQ", nullable = false)),
			@AttributeOverride(name = "sisSigla", column = @Column(name = "SIS_SIGLA", nullable = false, length = 3)) })
	public SgqOrigemId getId() {
		return this.id;
	}

	public void setId(SgqOrigemId id) {
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
	@JoinColumn(name = "QRY_SEQ", nullable = false, insertable = false, updatable = false)
	public SgqQuery getSgqQuery() {
		return this.sgqQuery;
	}

	public void setSgqQuery(SgqQuery sgqQuery) {
		this.sgqQuery = sgqQuery;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SIS_SIGLA", nullable = false, insertable = false, updatable = false)
	public AghSistemas getAghSistemas() {
		return this.aghSistemas;
	}

	public void setAghSistemas(AghSistemas aghSistemas) {
		this.aghSistemas = aghSistemas;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		SGQ_QUERIES("sgqQuery"),
		AGH_SISTEMAS("aghSistemas");

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
		if (!(obj instanceof SgqOrigem)) {
			return false;
		}
		SgqOrigem other = (SgqOrigem) obj;
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
