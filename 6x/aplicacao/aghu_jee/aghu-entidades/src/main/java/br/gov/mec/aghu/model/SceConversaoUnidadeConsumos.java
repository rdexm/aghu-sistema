package br.gov.mec.aghu.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "SCE_CONVERSAO_UNIDADE_CONSUMOS", schema = "AGH")
public class SceConversaoUnidadeConsumos extends BaseEntityId<SceConversaoUnidadeConsumosId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6948478645734491574L;
	
	private SceConversaoUnidadeConsumosId id; 
	private BigDecimal fatorConversao;      
	private ScoMaterial material;           
	private ScoUnidadeMedida unidadeMedida; 
	private Integer version;                
	private Date criadoEm;                  
	
	public SceConversaoUnidadeConsumos(SceConversaoUnidadeConsumosId id,
			BigDecimal fatorConversao, ScoMaterial material,
			ScoUnidadeMedida unidadeMedida, Integer version, Date criadoEm) {
		this.id = id;
		this.fatorConversao = fatorConversao;
		this.material = material;
		this.unidadeMedida = unidadeMedida;
		this.criadoEm = criadoEm;
	}
	
	public SceConversaoUnidadeConsumos(){
		
	}
	
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "matCodigo", column = @Column(name = "MAT_CODIGO", nullable = false, precision = 6, scale = 0)),
			@AttributeOverride(name = "umdCodigo", column = @Column(name = "UMD_CODIGO", length = 3, nullable = false)) })
	public SceConversaoUnidadeConsumosId getId() {
		return this.id;
	}

	public void setId(SceConversaoUnidadeConsumosId id) {
		this.id = id;
	}

	@Column(name = "FATOR_CONVERSAO", precision = 11, scale = 4)
	public BigDecimal getFatorConversao() {
		return fatorConversao;
	}

	public void setFatorConversao(BigDecimal fatorConversao) {
		this.fatorConversao = fatorConversao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MAT_CODIGO", nullable = false, insertable = false, updatable = false)
	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UMD_CODIGO", nullable = false, insertable = false, updatable = false)
	public ScoUnidadeMedida getUnidadeMedida() {
		return unidadeMedida;
	}

	public void setUnidadeMedida(ScoUnidadeMedida unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

	@Column(name = "VERSION", length = 7)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "CRIADO_EM", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
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
		SceConversaoUnidadeConsumos other = (SceConversaoUnidadeConsumos) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
	
	public enum Fields {
		
		MATERIAL("id.matCodigo"), 
		UNIDADE_MEDIDA("id.umdCodigo"),
		FATOR_CONVERSAO("fatorConversao"),
		SCO_MATERIAL("material"),
		SCO_UNIDADE_MEDIDA("unidadeMedida");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

}