package br.gov.mec.aghu.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "V_AEL_SER_SISMAMA", schema = "AGH")
@Immutable
public class VAelSerSismama extends BaseEntityId<VAelSerSismamaId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7787237418412163894L;
	private VAelSerSismamaId id;
	
	public VAelSerSismama() {
	}	
	
	public VAelSerSismama(VAelSerSismamaId id) {
		this.id = id;
	}	
	
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "vinCodigo", column = @Column(name = "SER_VIN_CODIGO")),
			@AttributeOverride(name = "matricula", column = @Column(name = "SER_MATRICULA")),
			@AttributeOverride(name = "nome", column = @Column(name = "NOME", length = 50)),
			@AttributeOverride(name = "sigla", column = @Column(name = "SIGLA_CONSELHO", length = 15)),
			@AttributeOverride(name = "nroRegConselho", column = @Column(name = "NRO_REG_CONSELHO", length = 9)) })
	public VAelSerSismamaId getId() {
		return id;
	}
	public void setId(VAelSerSismamaId id) {
		this.id = id;
	}
	
	public enum Fields {
		VIN_CODIGO("id.vinCodigo"),
		MATRICULA("id.matricula"),
		NOME("id.nome"),
		SIGLA("id.sigla"),
		NRO_REG_CONSELHO("id.nroRegConselho");
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		
		VAelSerSismama other = (VAelSerSismama) obj;
		
		if (id == null) {
			if (other.id != null) {
				return false;
			}	
		} else if (!id.equals(other.id)) {
			return false;
		}	
		return true;
	}	
}
