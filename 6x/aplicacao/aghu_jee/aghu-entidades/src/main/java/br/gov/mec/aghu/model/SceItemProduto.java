package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;


import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * ================================================================================
 *   ####   #####    ####   ######  #####   ##  ##   ####    ####    ####    #### 
 *  ##  ##  ##  ##  ##      ##      ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##
 *  ##  ##  #####    ####   ####    #####   ##  ##  ######  ##      ######  ##  ##
 *  ##  ##  ##  ##      ##  ##      ##  ##   ####   ##  ##  ##  ##  ##  ##  ##  ##
 *   ####   #####    ####   ######  ##  ##    ##    ##  ##   ####   ##  ##   #### 
 * ================================================================================
 *
 * A partir de uma análise originada pela tarefa #19993
 * esta model foi escolhida para ser apenas de leitura
 * no AGHU e por isso foi anotada como Immutable.
 *
 * Entretanto, caso esta entidade seja necessária na construção
 * de uma estória que necessite escrever dados no banco, este
 * comentário e esta anotação pode ser retirada desta model.
 */
@Immutable

@Entity
@Table(name = "SCE_ITEM_PRODUTOS", schema = "AGH")
public class SceItemProduto extends BaseEntityId<SceItemProdutoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5676574660571205513L;
	private SceItemProdutoId id;
	private Integer version;
	private ScoMaterial scoMaterialByMatCodigoMatPrima;
	private ScoMaterial scoMaterialByMatCodigo;
	private Double qtdeUtilizada;

	public SceItemProduto() {
	}

	public SceItemProduto(SceItemProdutoId id, ScoMaterial scoMaterialByMatCodigoMatPrima, ScoMaterial scoMaterialByMatCodigo,
			Double qtdeUtilizada) {
		this.id = id;
		this.scoMaterialByMatCodigoMatPrima = scoMaterialByMatCodigoMatPrima;
		this.scoMaterialByMatCodigo = scoMaterialByMatCodigo;
		this.qtdeUtilizada = qtdeUtilizada;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "matCodigo", column = @Column(name = "MAT_CODIGO", nullable = false)),
			@AttributeOverride(name = "matCodigoMatPrima", column = @Column(name = "MAT_CODIGO_MAT_PRIMA", nullable = false)) })
	public SceItemProdutoId getId() {
		return this.id;
	}

	public void setId(SceItemProdutoId id) {
		this.id = id;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MAT_CODIGO_MAT_PRIMA", nullable = false, insertable = false, updatable = false)
	public ScoMaterial getScoMaterialByMatCodigoMatPrima() {
		return this.scoMaterialByMatCodigoMatPrima;
	}

	public void setScoMaterialByMatCodigoMatPrima(ScoMaterial scoMaterialByMatCodigoMatPrima) {
		this.scoMaterialByMatCodigoMatPrima = scoMaterialByMatCodigoMatPrima;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MAT_CODIGO", nullable = false, insertable = false, updatable = false)
	public ScoMaterial getScoMaterialByMatCodigo() {
		return this.scoMaterialByMatCodigo;
	}

	public void setScoMaterialByMatCodigo(ScoMaterial scoMaterialByMatCodigo) {
		this.scoMaterialByMatCodigo = scoMaterialByMatCodigo;
	}

	@Column(name = "QTDE_UTILIZADA", nullable = false, precision = 17, scale = 17)
	public Double getQtdeUtilizada() {
		return this.qtdeUtilizada;
	}

	public void setQtdeUtilizada(Double qtdeUtilizada) {
		this.qtdeUtilizada = qtdeUtilizada;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		SCO_MATERIAL_BY_MAT_CODIGO_MAT_PRIMA("scoMaterialByMatCodigoMatPrima"),
		SCO_MATERIAL_BY_MAT_CODIGO("scoMaterialByMatCodigo"),
		QTDE_UTILIZADA("qtdeUtilizada");

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
		if (!(obj instanceof SceItemProduto)) {
			return false;
		}
		SceItemProduto other = (SceItemProduto) obj;
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
