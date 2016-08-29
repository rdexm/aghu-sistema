package br.gov.mec.aghu.faturamento.vo;

public class FatpTabCodeVO {

	private String subset;
	
	private Integer codigo;
	
	private String caracter;
	
	public FatpTabCodeVO() {
	}

	public FatpTabCodeVO(String subset, Integer codigo, String caracter) {

		this.subset = subset;
		this.codigo = codigo;
		this.caracter = caracter;
	}

	public String getSubset() {
		return subset;
	}

	public void setSubset(String subset) {
		this.subset = subset;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getCaracter() {
		return caracter;
	}

	public void setCaracter(String caracter) {
		this.caracter = caracter;
	}
}
