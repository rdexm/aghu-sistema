package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * BrasindiceEdicaoHist generated by hbm2java
 */
@Entity
@Immutable
@Table(name = "BRASINDICE_EDICOES", schema = "HIST")
public class BrasindiceEdicaoHist extends BaseEntityId<BrasindiceEdicaoHistId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7869400030182046364L;
	private BrasindiceEdicaoHistId id;

	public BrasindiceEdicaoHist() {
	}

	public BrasindiceEdicaoHist(BrasindiceEdicaoHistId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "edicao", column = @Column(name = "EDICAO")),
			@AttributeOverride(name = "dataInic", column = @Column(name = "DATA_INIC", length = 29)),
			@AttributeOverride(name = "dataFinl", column = @Column(name = "DATA_FINL", length = 29)) })
	public BrasindiceEdicaoHistId getId() {
		return this.id;
	}

	public void setId(BrasindiceEdicaoHistId id) {
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
		if (!(obj instanceof BrasindiceEdicaoHist)) {
			return false;
		}
		BrasindiceEdicaoHist other = (BrasindiceEdicaoHist) obj;
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