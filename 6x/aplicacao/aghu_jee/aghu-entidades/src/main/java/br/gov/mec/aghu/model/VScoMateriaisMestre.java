package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "V_SCO_MATERIAIS_MESTRE", schema = "CATMAT")
@Immutable
public class VScoMateriaisMestre implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3121081743584259133L;
	
	private String codigo;
	private String descricao;

	public VScoMateriaisMestre() {
		
	}
	
	public VScoMateriaisMestre(String codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	@Id
	@Column(name = "CODIGO", length = 9)
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
		if (!(obj instanceof VScoMateriaisMestre)) {
			return false;
		}
		VScoMateriaisMestre other = (VScoMateriaisMestre) obj;
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
