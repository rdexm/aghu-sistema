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
@Table(name = "SGQ_TABELAS_UTILIZADAS", schema = "AGH")
public class SgqTabelaUtilizada extends BaseEntityId<SgqTabelaUtilizadaId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8985191913717259544L;
	private SgqTabelaUtilizadaId id;
	private Integer version;
	private SgqQuery sgqQuery;

	public SgqTabelaUtilizada() {
	}

	public SgqTabelaUtilizada(SgqTabelaUtilizadaId id, SgqQuery sgqQuery) {
		this.id = id;
		this.sgqQuery = sgqQuery;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "qrySeq", column = @Column(name = "QRY_SEQ", nullable = false)),
			@AttributeOverride(name = "tabela", column = @Column(name = "TABELA", nullable = false, length = 30)) })
	public SgqTabelaUtilizadaId getId() {
		return this.id;
	}

	public void setId(SgqTabelaUtilizadaId id) {
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

	public enum Fields {

		ID("id"),
		VERSION("version"),
		SGQ_QUERIES("sgqQuery");

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
		if (!(obj instanceof SgqTabelaUtilizada)) {
			return false;
		}
		SgqTabelaUtilizada other = (SgqTabelaUtilizada) obj;
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
