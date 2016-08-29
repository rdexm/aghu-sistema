package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import br.gov.mec.aghu.core.persistence.BaseEntityId;


/**
 * The persistent class for the sco_marca_modelo_materiais database table.
 * 
 */
@Entity
@Table(name="SCO_MARCA_MODELO_MATERIAIS")
public class ScoMarcaModeloMaterial extends BaseEntityId<ScoMarcaModeloMaterialId> implements Serializable {	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3152839274462553700L;
	private ScoMarcaModeloMaterialId id;
	private ScoMarcaModelo scoMarcaModelo;
	private Integer version;

    public ScoMarcaModeloMaterial() {
    }


	@EmbeddedId
	public ScoMarcaModeloMaterialId getId() {
		return this.id;
	}

	public void setId(ScoMarcaModeloMaterialId id) {
		this.id = id;
	}
	

	//bi-directional many-to-one association to ScoMarcaModelo
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name="MOM_MCM_CODIGO", referencedColumnName="MCM_CODIGO", insertable=false, updatable=false),
		@JoinColumn(name="MOM_SEQP", referencedColumnName="SEQP", insertable=false, updatable=false)
		})
	public ScoMarcaModelo getScoMarcaModelo() {
		return this.scoMarcaModelo;
	}

	public void setScoMarcaModelo(ScoMarcaModelo scoMarcaModelo) {
		this.scoMarcaModelo = scoMarcaModelo;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}	
	
	public enum Fields {

		ID("id"),
		MAT_CODIGO("id.matCodigo"),
		MCM_CODIGO("id.momMcmCodigo"),
		MOM_SEQP("id.momSeqp"),
		SCO_MARCA_MODELO("scoMarcaModelo");

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
		if (!(obj instanceof ScoMarcaModeloMaterial)) {
			return false;
		}
		ScoMarcaModeloMaterial other = (ScoMarcaModeloMaterial) obj;
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