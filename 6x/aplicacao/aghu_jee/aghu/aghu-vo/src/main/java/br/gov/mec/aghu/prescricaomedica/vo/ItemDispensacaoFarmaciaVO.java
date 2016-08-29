package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;

public class ItemDispensacaoFarmaciaVO implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7046424067037617946L;
	/**
	 * 
	 */

	
	private String codigoItemMedicamento;
	private String descricaoItemMedicamento;
	private String qtdSolicitada;
	
	
	public String getCodigoItemMedicamento() {
		return codigoItemMedicamento;
	}
	public void setCodigoItemMedicamento(String codigoItemMedicamento) {
		this.codigoItemMedicamento = codigoItemMedicamento;
	}
	public String getDescricaoItemMedicamento() {
		return descricaoItemMedicamento;
	}
	public void setDescricaoItemMedicamento(String descricaoItemMedicamento) {
		this.descricaoItemMedicamento = descricaoItemMedicamento;
	}
	public String getQtdSolicitada() {
		return qtdSolicitada;
	}
	public void setQtdSolicitada(String qtdSolicitada) {
		this.qtdSolicitada = qtdSolicitada;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
	

}
