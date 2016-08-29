package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import br.gov.mec.aghu.core.persistence.BaseEntityId;


/**
 * The persistent class for the sce_consumo_total_materiais database table.
 * 
 */
@Entity
@Table(name="SCE_CONSUMO_TOTAL_MATERIAIS")
public class SceConsumoTotalMaterial extends BaseEntityId<SceConsumoTotalMaterialId> implements Serializable {	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1764342561857399330L;
	private SceConsumoTotalMaterialId id;
	private Integer quantidade;
	private Double valor;
	private Integer version;
	private FccCentroCustos centroCusto;
	private ScoMaterial material;
	private SceAlmoxarifado almoxarifado;
	
    public SceConsumoTotalMaterial() {
    }

	@EmbeddedId
	public SceConsumoTotalMaterialId getId() {
		return this.id;
	}

	public void setId(SceConsumoTotalMaterialId id) {
		this.id = id;
	}
	

	public Integer getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}


	public Double getValor() {
		return this.valor;
	}

	public void setValor(Double valor) {
		if(valor!=null){
			BigDecimal valorTruncado;
			valorTruncado =  new BigDecimal(valor).setScale(2,RoundingMode.DOWN);
			this.valor = valorTruncado.doubleValue();
		}else{
			this.valor = valor;
		}
		
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	//bi-directional many-to-one association to FccCentroCusto
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="CCT_CODIGO", referencedColumnName = "CODIGO", insertable=false, updatable=false)
	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}


	//bi-directional many-to-one association to ScoMaterial
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="MAT_CODIGO", referencedColumnName = "CODIGO", insertable=false, updatable=false)
	public ScoMaterial getMaterial() {
		return material;
	}


	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}
	
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="ALM_SEQ", referencedColumnName = "SEQ", insertable=false, updatable=false)
	public SceAlmoxarifado getAlmoxarifado() {
		return this.almoxarifado;
	}

	public void setAlmoxarifado(SceAlmoxarifado almoxarifado) {
		this.almoxarifado = almoxarifado;
	}
	
	public enum Fields{
		ALMOXARIFADO("almoxarifado"),
		CENTRO_CUSTO("centroCusto"),
		MATERIAL("material"),
		QUANTIDADE("quantidade"),
		VALOR("valor"),
		DATA_COMPETENCIA("id.dtCompetencia"),
		ALMOXARIFADO_SEQ("id.almSeq");
		
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
		if (!(obj instanceof SceConsumoTotalMaterial)) {
			return false;
		}
		SceConsumoTotalMaterial other = (SceConsumoTotalMaterial) obj;
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