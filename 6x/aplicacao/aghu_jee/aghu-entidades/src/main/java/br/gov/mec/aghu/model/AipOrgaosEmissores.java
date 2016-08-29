package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;

@Entity
@Table(name = "AIP_ORGAOS_EMISSORES", schema = "AGH")
public class AipOrgaosEmissores extends BaseEntityCodigo<Short> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8822131998872652254L;
	private Short codigo;
	private String descricao;
	private Integer version;

	public AipOrgaosEmissores() {
	}

	public AipOrgaosEmissores(Short codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	@Id
	@Column(name = "CODIGO", nullable = false, precision = 3, scale = 0)
	public Short getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public enum Fields {
		CODIGO("codigo"), DESCRICAO("descricao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
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
		if (!(obj instanceof AipOrgaosEmissores)) {
			return false;
		}
		AipOrgaosEmissores other = (AipOrgaosEmissores) obj;
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