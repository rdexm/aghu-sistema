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
 * LwsResultado generated by hbm2java
 */
@Entity
@Table(name = "LWS_RESULTADOS", schema = "AGH")
public class LwsResultado extends BaseEntityId<LwsResultadoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7983081652475360413L;
	private LwsResultadoId id;

	public LwsResultado() {
	}

	public LwsResultado(LwsResultadoId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "ID", nullable = false)),
			@AttributeOverride(name = "ord", column = @Column(name = "ORD")),
			@AttributeOverride(name = "resultado", column = @Column(name = "RESULTADO", length = 2000)),
			@AttributeOverride(name = "term", column = @Column(name = "TERM", length = 1)),
			@AttributeOverride(name = "version", column = @Column(name = "VERSION", nullable = false)) })
	public LwsResultadoId getId() {
		return this.id;
	}

	public void setId(LwsResultadoId id) {
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
