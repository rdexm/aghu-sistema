package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import br.gov.mec.aghu.core.persistence.BaseEntity;


@Entity
@Table(name = "RAP_COD_STARH_LIVRES", schema = "AGH")
public class RapCodStarhLivres implements BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9025832570345246711L;
	private Integer codStarh;
	private Integer version;
	
	// getters & setters

	public RapCodStarhLivres() {
	}

	public RapCodStarhLivres(Integer codStarh, Integer version) {
		this.codStarh = codStarh;
		this.version = version;
	}

	@Id
	@Column(name = "COD_STARH", nullable = false, precision = 8, scale = 0)
	public Integer getCodStarh() {
		return codStarh;
	}

	public void setCodStarh(Integer codStarh) {
		this.codStarh = codStarh;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	// outros

	public enum Fields {
		COD_STARH("codStarh");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((codStarh == null) ? 0 : codStarh.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		RapCodStarhLivres other = (RapCodStarhLivres) obj;
		if (codStarh == null) {
			if (other.codStarh != null) {
				return false;
			}
		} else if (!codStarh.equals(other.codStarh)) {
			return false;
		}
		return true;
	}

	
}