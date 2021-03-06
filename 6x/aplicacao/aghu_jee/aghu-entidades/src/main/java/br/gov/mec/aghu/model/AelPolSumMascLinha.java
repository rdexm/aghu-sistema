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
 * AelPolSumMascLinha generated by hbm2java
 */
@Entity
@Table(name = "AEL_POL_SUM_MASC_LINHA", schema = "AGH")
public class AelPolSumMascLinha extends BaseEntityId<AelPolSumMascLinhaId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2470287168215919987L;
	private AelPolSumMascLinhaId id;

	public AelPolSumMascLinha() {
	}

	public AelPolSumMascLinha(AelPolSumMascLinhaId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "ordemRelatorio", column = @Column(name = "ORDEM_RELATORIO", nullable = false)),
			@AttributeOverride(name = "nroLinha", column = @Column(name = "NRO_LINHA", nullable = false)),
			@AttributeOverride(name = "descricao", column = @Column(name = "DESCRICAO", length = 2000)),
			@AttributeOverride(name = "nroSessao", column = @Column(name = "NRO_SESSAO", nullable = false)),
			@AttributeOverride(name = "version", column = @Column(name = "VERSION", nullable = false)) })
	public AelPolSumMascLinhaId getId() {
		return this.id;
	}

	public void setId(AelPolSumMascLinhaId id) {
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
