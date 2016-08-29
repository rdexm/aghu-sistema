package br.gov.mec.aghu.faturamento.vo;

public class FatpTabCodBarrasVO {

	private String barras;
	private String codigo;
	private Integer tamanhoString;
	private String campoString;
	private Integer calculo;
	private Integer resto;
	private String digito;
	private Integer calculoAux;
	private String calculoAuxDv;
	
	public FatpTabCodBarrasVO(){
		
	}
	
	public String getBarras() {
		return barras;
	}
	public void setBarras(String barras) {
		this.barras = barras;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public Integer getTamanhoString() {
		return tamanhoString;
	}
	public void setTamanhoString(Integer tamanhoString) {
		this.tamanhoString = tamanhoString;
	}
	public String getCampoString() {
		return campoString;
	}
	public void setCampoString(String campoString) {
		this.campoString = campoString;
	}
	public Integer getCalculo() {
		return calculo;
	}
	public void setCalculo(Integer calculo) {
		this.calculo = calculo;
	}
	public Integer getResto() {
		return resto;
	}
	public void setResto(Integer resto) {
		this.resto = resto;
	}
	public String getDigito() {
		return digito;
	}
	public void setDigito(String digito) {
		this.digito = digito;
	}
	public Integer getCalculoAux() {
		return calculoAux;
	}
	public void setCalculoAux(Integer calculoAux) {
		this.calculoAux = calculoAux;
	}
	public String getCalculoAuxDv() {
		return calculoAuxDv;
	}
	public void setCalculoAuxDv(String calculoAuxDv) {
		this.calculoAuxDv = calculoAuxDv;
	}
	
}
