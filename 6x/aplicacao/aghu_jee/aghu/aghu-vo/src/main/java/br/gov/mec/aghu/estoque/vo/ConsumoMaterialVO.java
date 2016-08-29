/**
 * 
 */
package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.ScoMaterial;

/**
 * @author bruno.mourao
 *
 */
public class ConsumoMaterialVO implements Serializable {
	
	private static final long serialVersionUID = -6513655304066109286L;
	public enum TipoConsumo{
		MEDIO_SEMESTRE,
		ULT_30_DIAS;
	}

	
	public ConsumoMaterialVO(Double cons, ScoMaterial mat, TipoConsumo tipCons){
		this.consumo = cons;
		this.material = mat;
		this.tipoConsumo = tipCons;
	}
	
	private Double consumo;
	private ScoMaterial material;
	private TipoConsumo tipoConsumo;
	
	public Double getConsumo() {
		return consumo;
	}
	public void setConsumo(Double consumo) {
		this.consumo = consumo;
	}
	public ScoMaterial getMaterial() {
		return material;
	}
	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}
	public TipoConsumo getTipoConsumo() {
		return tipoConsumo;
	}
	public void setTipoConsumo(TipoConsumo tipoConsumo) {
		this.tipoConsumo = tipoConsumo;
	}
	                    

}
