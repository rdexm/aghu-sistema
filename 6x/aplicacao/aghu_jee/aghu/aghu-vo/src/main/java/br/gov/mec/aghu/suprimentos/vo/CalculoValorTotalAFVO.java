package br.gov.mec.aghu.suprimentos.vo;

public class CalculoValorTotalAFVO {
	
	private Double valorBruto;
	private Double valorIPI;
	private Double valorLiq;
	private Double valorDesc;
	private Double valorAcresc;
	private Double valorEfetivo;
	private Double valorTotalAF;
	
	public Double getValorTotalAF() {
		return valorTotalAF;
	}
	public void setValorTotalAF(Double valorTotalAF) {
		this.valorTotalAF = valorTotalAF;
	}
	public Double getValorBruto() {
		return valorBruto;
	}
	public void setValorBruto(Double valorBruto) {
		this.valorBruto = valorBruto;
	}
	public Double getValorIPI() {
		return valorIPI;
	}
	public void setValorIPI(Double valorIPI) {
		this.valorIPI = valorIPI;
	}
	public Double getValorLiq() {
		return valorLiq;
	}
	public void setValorLiq(Double valorLiq) {
		this.valorLiq = valorLiq;
	}
	public Double getValorDesc() {
		return valorDesc;
	}
	public void setValorDesc(Double valorDesc) {
		this.valorDesc = valorDesc;
	}
	public Double getValorAcresc() {
		return valorAcresc;
	}
	public void setValorAcresc(Double valorAcresc) {
		this.valorAcresc = valorAcresc;
	}
	public Double getValorEfetivo() {
		return valorEfetivo;
	}
	public void setValorEfetivo(Double valorEfetivo) {
		this.valorEfetivo = valorEfetivo;
	}
}
