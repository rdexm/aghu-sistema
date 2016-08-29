/**
 * 
 */
package br.gov.mec.aghu.suprimentos.vo;

import java.util.List;

/**
 * @author bruno.mourao
 *
 */
public class RelUltimasComprasPACVOPai {
	
	private Short nroItem;
	private Integer codMaterial;
	private String descMaterial;
	private String descUnidade;
	List<RelUltimasComprasPACVO> compras;
	
	
	/**
	 * @return the compras
	 */
	public List<RelUltimasComprasPACVO> getCompras() {
		return compras;
	}
	/**
	 * @param compras the compras to set
	 */
	public void setCompras(List<RelUltimasComprasPACVO> compras) {
		this.compras = compras;
	}
	public Short getNroItem() {
		return nroItem;
	}
	public void setNroItem(Short nroItem) {
		this.nroItem = nroItem;
	}
	public Integer getCodMaterial() {
		return codMaterial;
	}
	public void setCodMaterial(Integer codMaterial) {
		this.codMaterial = codMaterial;
	}
	public String getDescMaterial() {
		return descMaterial;
	}
	public void setDescMaterial(String descMaterial) {
		this.descMaterial = descMaterial;
	}
	public String getDescUnidade() {
		return descUnidade;
	}
	public void setDescUnidade(String descUnidade) {
		this.descUnidade = descUnidade;
	}

}
