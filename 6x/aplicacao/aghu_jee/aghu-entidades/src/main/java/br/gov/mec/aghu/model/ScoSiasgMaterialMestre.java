package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;

@Entity
@Table(name = "SCO_SIASG_MATERIAL_MESTRE", schema = "AGH")
public class ScoSiasgMaterialMestre extends BaseEntityCodigo<String> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1611468356331691044L;
	
	private String codigo;
	private String descricao;
	
	public ScoSiasgMaterialMestre() { }

	public ScoSiasgMaterialMestre(String codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	@Id
	@Column(name = "CODIGO", length = 9, nullable = false)
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	@Column(name = "DESCRICAO")
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@Transient
	public Integer getCodigoMaterial(){
		Integer codMat = Integer.parseInt(this.codigo.replaceAll( "\\D*", "" ));
		return codMat;
	}
	
	public enum Fields {

		CODIGO("codigo"),
		DESCRICAO("descricao");

		private String fields;

		private Fields(String fields) {
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
		if (!(obj instanceof ScoSiasgMaterialMestre)) {
			return false;
		}
		ScoSiasgMaterialMestre other = (ScoSiasgMaterialMestre) obj;
		if (getCodigo() == null) {
			if (other.getCodigo() != null) {
				return false;
			}
		} else if (!getCodigo().equals(other.getCodigo())) {
			return false;
		}
		return true;
	}

}
