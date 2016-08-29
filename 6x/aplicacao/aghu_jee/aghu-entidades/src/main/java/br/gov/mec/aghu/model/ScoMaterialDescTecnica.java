package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;

@Entity
@Table(name = "SCO_MATERIAIS_DESC_TECNICAS", schema = "AGH")
@SequenceGenerator(name = "scoMdtSq1", sequenceName = "AGH.SCO_MDT_SQ1", allocationSize = 1)
public class ScoMaterialDescTecnica  extends BaseEntityCodigo<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8782968399065456194L;
	
	private Integer codigo;
	private ScoMaterial material;
	private ScoDescricaoTecnicaPadrao descricao;
	private Integer version;
	
	public ScoMaterialDescTecnica() {}
	
	@Id
	@Column(name = "CODIGO", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoMdtSq1")
	public Integer getCodigo() {
		return codigo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CODIGO_MATERIAL", referencedColumnName = "CODIGO", nullable = false)
	public ScoMaterial getMaterial() {
		return material;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CODIGO_DESCRICAO", referencedColumnName = "CODIGO",  nullable = false)
	public ScoDescricaoTecnicaPadrao getDescricao() {
		return descricao;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}
	
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	
	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}
	
	public void setDescricao(ScoDescricaoTecnicaPadrao descricao) {
		this.descricao = descricao;
	}
	
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public enum Fields {

		CODIGO("codigo"),
		VERSION("version"),
		MATERIAL("material"),
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
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
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
		ScoMaterialDescTecnica other = (ScoMaterialDescTecnica) obj;
		if (codigo == null) {
			if (other.codigo != null) {
				return false;
			}
		} else if (!codigo.equals(other.codigo)) {
			return false;
		}
		return true;
	}
	
	
	
}