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
 * TblExmeDltc generated by hbm2java
 */
@Entity
@Table(name = "TBL_EXME_DLTC", schema = "AGH")
public class TblExmeDltc extends BaseEntityId<TblExmeDltcId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5649863011145108456L;
	private TblExmeDltcId id;

	public TblExmeDltc() {
	}

	public TblExmeDltc(TblExmeDltcId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "codExme", column = @Column(name = "COD_EXME", nullable = false)),
			@AttributeOverride(name = "nomeExme", column = @Column(name = "NOME_EXME", nullable = false, length = 100)),
			@AttributeOverride(name = "version", column = @Column(name = "VERSION", nullable = false)) })
	public TblExmeDltcId getId() {
		return this.id;
	}

	public void setId(TblExmeDltcId id) {
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