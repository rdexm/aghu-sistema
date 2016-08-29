package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;

public class ValidaUnidadeMaterialVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6724215315188611639L;
	/**
	 * 
	 */
	private Short itlNumero;
	private Integer matCodigo;
	private String umdCodigoIaf;
	private String umdCodigoEal;
	
	
	public ValidaUnidadeMaterialVO(Short itlNumero, Integer matCodigo,
			String umdCodigoIaf, String umdCodigoEal) {
		super();
		this.itlNumero = itlNumero;
		this.matCodigo = matCodigo;
		this.umdCodigoIaf = umdCodigoIaf;
		this.umdCodigoEal = umdCodigoEal;
	}
	
	public Short getItlNumero() {
		return itlNumero;
	}
	public void setItlNumero(Short itlNumero) {
		this.itlNumero = itlNumero;
	}
	public Integer getMatCodigo() {
		return matCodigo;
	}
	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}
	public String getUmdCodigoIaf() {
		return umdCodigoIaf;
	}
	public void setUmdCodigoIaf(String umdCodigoIaf) {
		this.umdCodigoIaf = umdCodigoIaf;
	}
	public String getUmdCodigoEal() {
		return umdCodigoEal;
	}
	public void setUmdCodigoEal(String umdCodigoEal) {
		this.umdCodigoEal = umdCodigoEal;
	}
	
	

}
