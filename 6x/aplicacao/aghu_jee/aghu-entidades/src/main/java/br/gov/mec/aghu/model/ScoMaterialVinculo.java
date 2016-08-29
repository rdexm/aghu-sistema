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


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;

@Entity
@Table(name = "SCO_MATERIAIS_VINCULOS", schema = "AGH")
@SequenceGenerator(name = "scoMatVincSq1", sequenceName = "agh.sco_mvc_sq1", allocationSize = 1)
public class ScoMaterialVinculo extends BaseEntityCodigo<Integer> implements Serializable {

	public ScoMaterialVinculo() {
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3851242990348811197L;

	/**
	 * Chave primaria
	 */
	private Integer codigo;
	/**
	 * Material
	 */
	private ScoMaterial material;

	/**
	 * Material vinculo
	 */
	private ScoMaterial materialVinculo;

	/**
	 * version
	 */
	private Integer version;

	@Id
	@Column(name = "CODIGO", unique = true, nullable = false, precision = 6, scale = 0)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoMatVincSq1")
	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CODIGO_MATERIAL", referencedColumnName = "CODIGO", nullable = false)
	public ScoMaterial getMaterial() {
		return material;
	}

	public ScoMaterialVinculo(ScoMaterial material, ScoMaterial materialVinculo) {
		super();
		this.material = material;
		this.materialVinculo = materialVinculo;
	}

	public void setMaterialVinculo(ScoMaterial materialVinculo) {
		this.materialVinculo = materialVinculo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CODIGO_VINCULO", referencedColumnName = "CODIGO", nullable = false)
	public ScoMaterial getMaterialVinculo() {
		return materialVinculo;
	}

	public int hashCode() {
		return new HashCodeBuilder().append(this.codigo).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ScoMaterialVinculo)) {
			return false;
		}
		ScoMaterialVinculo castOther = (ScoMaterialVinculo) other;
		return new EqualsBuilder().append(this.codigo, castOther.getCodigo())
				.isEquals();
	}

	public String toString() {
		return new ToStringBuilder(this).append(Fields.CODIGO.toString(),
				this.codigo).toString();
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Version
	@Column(name = "VERSION", nullable = false, precision = 9)
	public Integer getVersion() {
		return version;
	}

	public enum Fields {

		CODIGO("codigo"), 
		MATERIAL("material"),
		MATERIAL_CODIGO("material.codigo"),
		MATERIAL_VINCULO("materialVinculo.codigo"),
		MATERIAL_VINC("materialVinculo");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}

	}

}
