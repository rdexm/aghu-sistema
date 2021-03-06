package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * ScoSemMarca generated by hbm2java
 */
@Entity
@Table(name = "SCO_SEM_MARCA", schema = "AGH")
public class ScoSemMarca extends BaseEntityId<ScoSemMarcaId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7602239230778405039L;
	private ScoSemMarcaId id;

	public ScoSemMarca() {
	}

	public ScoSemMarca(ScoSemMarcaId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "licitacao", column = @Column(name = "LICITACAO", nullable = false)),
			@AttributeOverride(name = "item", column = @Column(name = "ITEM", nullable = false)),
			@AttributeOverride(name = "fornecedor", column = @Column(name = "FORNECEDOR", nullable = false)),
			@AttributeOverride(name = "marca", column = @Column(name = "MARCA", length = 45)),
			@AttributeOverride(name = "version", column = @Column(name = "VERSION", nullable = false)) })
	public ScoSemMarcaId getId() {
		return this.id;
	}

	public void setId(ScoSemMarcaId id) {
		this.id = id;
	}

	public enum Fields {

		ID("id");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

}
