package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;


/**
 * The persistent class for the sco_caracteristicas database table.
 * 
 */
@Entity
@Table(name="SCO_CARACTERISTICAS")
public class ScoCaracteristica extends BaseEntityCodigo<Integer> implements Serializable {	/**
	 * 
	 */
	private static final long serialVersionUID = -1176247237415051438L;
 
	private Integer codigo;
	private String caracteristica;
	private Integer version;
	private Set<ScoCaracteristicaUsuarioCentroCusto> caracteristicasUsuarioCcusto;

    public ScoCaracteristica() {
    }


	@Id
	@SequenceGenerator(name="SCO_CARACTERISTICAS_CODIGO_GENERATOR" , allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SCO_CARACTERISTICAS_CODIGO_GENERATOR")
	public Integer getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}


	public String getCaracteristica() {
		return this.caracteristica;
	}

	public void setCaracteristica(String caracteristica) {
		this.caracteristica = caracteristica;
	}


	@Version
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@OneToMany(mappedBy="caracteristica")
	public Set<ScoCaracteristicaUsuarioCentroCusto> getCaracteristicasUsuarioCcusto() {
		return caracteristicasUsuarioCcusto;
	}


	public void setCaracteristicasUsuarioCcusto(
			Set<ScoCaracteristicaUsuarioCentroCusto> caracteristicasUsuarioCcusto) {
		this.caracteristicasUsuarioCcusto = caracteristicasUsuarioCcusto;
	}



	/**
	 * 
	 *
	 */
	public enum Fields {
		CODIGO("codigo"), 
		CARACTERISTICA("caracteristica");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
	

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getCodigo() == null) ? 0 : getCodigo().hashCode());
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
		if (!(obj instanceof ScoCaracteristica)) {
			return false;
		}
		ScoCaracteristica other = (ScoCaracteristica) obj;
		if (getCodigo() == null) {
			if (other.getCodigo() != null) {
				return false;
			}
		} else if (!getCodigo().equals(other.getCodigo())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}