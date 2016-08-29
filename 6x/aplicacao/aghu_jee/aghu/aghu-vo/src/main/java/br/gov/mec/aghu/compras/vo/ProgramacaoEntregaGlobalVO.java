package br.gov.mec.aghu.compras.vo;

import java.math.BigDecimal;

public abstract class ProgramacaoEntregaGlobalVO implements Comparable<ProgramacaoEntregaGlobalVO> {
	private BigDecimal saldoProgramado;
	private BigDecimal valorALiberar;
	private BigDecimal valorLiberado;
	private BigDecimal valorEmAtraso;
	private Double percentualAtraso;
	
	private String corSaldoProgramado;
	private String corValorALiberar;
	private String corValorLiberado;
	private String corValorEmAtraso;
	
	public String getCorSaldoProgramado() {
		if (saldoProgramado != null && saldoProgramado.doubleValue() > 0) {
			corSaldoProgramado = "background-color: #95BCF2;";
		} else {
			corSaldoProgramado = "";
		}
		return corSaldoProgramado;
	}
	public String getCorValorALiberar() {
		if (valorALiberar != null && valorALiberar.doubleValue() > 0) {
			corValorALiberar = "background-color: #FFFF80;";
		} else {
			corValorALiberar = "";
		}
		return corValorALiberar;
	}
	public String getCorValorLiberado() {
		if (valorLiberado != null && valorLiberado.doubleValue() > 0) {
			corValorLiberado = "background-color: #80CF87;";
		} else {
			corValorLiberado = "";
		}
		return corValorLiberado;
	}
	public String getCorValorEmAtraso() {
		if (valorEmAtraso != null && valorEmAtraso.doubleValue() > 0) {
			corValorEmAtraso = "background-color: #E68080;";
		} else {
			corValorEmAtraso = "";
		}
		return corValorEmAtraso;
	}
	
	public BigDecimal getSaldoProgramado() {
		return saldoProgramado;
	}
	
	public void setSaldoProgramado(BigDecimal saldoProgramado) {
		this.saldoProgramado = saldoProgramado;
	}
	public BigDecimal getValorALiberar() {
		return valorALiberar;
	}
	public void setValorALiberar(BigDecimal valorALiberar) {
		this.valorALiberar = valorALiberar;
	}
	public BigDecimal getValorLiberado() {
		return valorLiberado;
	}
	public void setValorLiberado(BigDecimal valorLiberado) {
		this.valorLiberado = valorLiberado;
	}
	public BigDecimal getValorEmAtraso() {
		return valorEmAtraso;
	}
	public void setValorEmAtraso(BigDecimal valorEmAtraso) {
		this.valorEmAtraso = valorEmAtraso;
	}
	public void setCorSaldoProgramado(String corSaldoProgramado) {
		this.corSaldoProgramado = corSaldoProgramado;
	}
	public void setCorValorALiberar(String corValorALiberar) {
		this.corValorALiberar = corValorALiberar;
	}
	public void setCorValorLiberado(String corValorLiberado) {
		this.corValorLiberado = corValorLiberado;
	}
	public void setCorValorEmAtraso(String corValorEmAtraso) {
		this.corValorEmAtraso = corValorEmAtraso;
	}
	public Double getPercentualAtraso() {
		percentualAtraso = 0d;
		if (valorLiberado != null && valorLiberado.doubleValue() > 0) {
			percentualAtraso = (double)((valorEmAtraso ==  null ? 0d : valorEmAtraso.doubleValue() / valorLiberado.doubleValue()));
		}
		return percentualAtraso;
	}
	public void setPercentualAtraso(Double percentualAtraso) {
		this.percentualAtraso = percentualAtraso;
	}
	
	@Override
	public int compareTo(ProgramacaoEntregaGlobalVO compareValue) {
		if(valorLiberado != null && compareValue.getValorLiberado() != null) {
			return compareValue.getValorLiberado().compareTo(valorLiberado);
		}
		return 0;
	}
}
