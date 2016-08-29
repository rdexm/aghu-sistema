package br.gov.mec.aghu.compras.contaspagar.vo;

import java.io.Serializable;
import java.util.List;

public class RelatorioDividaResumoCsvVO  implements Serializable {
	
	private static final long serialVersionUID = 7454866835243274468L;
	
	private List<RelatorioDividaResumoVO> titulos;
	private Double total;
	
	public RelatorioDividaResumoCsvVO(List<RelatorioDividaResumoVO> titulos, Double total) {
		super();
		this.titulos = titulos;
		this.total = total;
	}
	/**
	 * @return the titulos
	 */
	public List<RelatorioDividaResumoVO> getTitulos() {
		return titulos;
	}
	/**
	 * @param titulos the titulos to set
	 */
	public void setTitulos(List<RelatorioDividaResumoVO> titulos) {
		this.titulos = titulos;
	}
	/**
	 * @return the total
	 */
	public Double getTotal() {
		return total;
	}
	/**
	 * @param total the total to set
	 */
	public void setTotal(Double total) {
		this.total = total;
	}
		
	
}
