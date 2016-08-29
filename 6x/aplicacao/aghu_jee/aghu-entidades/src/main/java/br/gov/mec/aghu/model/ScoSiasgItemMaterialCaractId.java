package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;



@Embeddable
public class ScoSiasgItemMaterialCaractId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 644041600984112913L;
	
	private String itNuItemMaterialCaract;
	private String itCoCaractItem;

	public ScoSiasgItemMaterialCaractId() {

	}
	
	public ScoSiasgItemMaterialCaractId(String itNuItemMaterialCaract, String itCoCaractItem) {

		this.itNuItemMaterialCaract = itNuItemMaterialCaract;
		this.itCoCaractItem = itCoCaractItem;
	}

	@Column(name = "IT_NU_ITEM_MATERIAL_CARACT", length = 9, nullable = false)
	public String getItNuItemMaterialCaract() {
		return itNuItemMaterialCaract;
	}

	public void setItNuItemMaterialCaract(String itNuItemMaterialCaract) {
		this.itNuItemMaterialCaract = itNuItemMaterialCaract;
	}

	@Column(name = "IT_CO_CARACT_ITEM", length = 4, nullable = false)
	public String getItCoCaractItem() {
		return itCoCaractItem;
	}

	public void setItCoCaractItem(String itCoCaractItem) {
		this.itCoCaractItem = itCoCaractItem;
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getItNuItemMaterialCaract());
		umHashCodeBuilder.append(this.getItCoCaractItem());
		return umHashCodeBuilder.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ScoSiasgItemMaterialCaractId)) {
			return false;
		}
		ScoSiasgItemMaterialCaractId other = (ScoSiasgItemMaterialCaractId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getItNuItemMaterialCaract(), other.getItNuItemMaterialCaract());
		umEqualsBuilder.append(this.getItCoCaractItem(), other.getItCoCaractItem());
		return umEqualsBuilder.isEquals();
	}

}
