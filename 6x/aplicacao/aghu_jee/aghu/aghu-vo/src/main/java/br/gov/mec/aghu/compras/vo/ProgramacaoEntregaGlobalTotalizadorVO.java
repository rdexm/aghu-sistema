package br.gov.mec.aghu.compras.vo;

import java.math.BigDecimal;

public class ProgramacaoEntregaGlobalTotalizadorVO {
	public BigDecimal totalSaldoProgramado;
	public BigDecimal totalValorALiberar;
	public BigDecimal totalValorLiberado;
	public BigDecimal totalValorEmAtraso;
	
	public ProgramacaoEntregaGlobalTotalizadorVO() {
		totalSaldoProgramado = BigDecimal.ZERO;
		totalValorALiberar = BigDecimal.ZERO;
		totalValorLiberado = BigDecimal.ZERO;
		totalValorEmAtraso = BigDecimal.ZERO;
	}
	
	private String corTotalSaldoProgramado;
	private String corTotalValorALiberar;
	private String corTotalValorLiberado;
	private String corTotalValorEmAtraso;
	
	public String getCorTotalSaldoProgramado() {
		if (totalSaldoProgramado != null && totalSaldoProgramado.doubleValue() > 0) {
			corTotalSaldoProgramado = "background-color: #95BCF2 !important;";
		} else {
			corTotalSaldoProgramado = "";
		}
		return corTotalSaldoProgramado;
	}
	public String getCorTotalValorALiberar() {
		if (totalValorALiberar != null && totalValorALiberar.doubleValue() > 0) {
			corTotalValorALiberar = "background-color: #FFFF80 !important;";
		} else {
			corTotalValorALiberar = "";
		}
		return corTotalValorALiberar;
	}
	public String getCorTotalValorLiberado() {
		if (totalValorLiberado.doubleValue() > 0) {
			corTotalValorLiberado = "background-color: #80CF87 !important;";
		} else {
			corTotalValorLiberado = "";
		}
		return corTotalValorLiberado;
	}
	public String getCorTotalValorEmAtraso() {
		if (totalValorEmAtraso != null && totalValorEmAtraso.doubleValue() > 0) {
			corTotalValorEmAtraso = "background-color: #E68080 !important;";
		} else {
			corTotalValorEmAtraso = "";
		}
		return corTotalValorEmAtraso;
	}
	
	public BigDecimal getTotalSaldoProgramado() {
		return totalSaldoProgramado;
	}
	public void setTotalSaldoProgramado(BigDecimal totalSaldoProgramado) {
		this.totalSaldoProgramado = totalSaldoProgramado;
	}
	public BigDecimal getTotalValorLiberado() {
		return totalValorLiberado;
	}
	public void setTotalValorLiberado(BigDecimal totalValorLiberado) {
		this.totalValorLiberado = totalValorLiberado;
	}
	public BigDecimal getTotalValorEmAtraso() {
		return totalValorEmAtraso;
	}
	public void setTotalValorEmAtraso(BigDecimal totalValorEmAtraso) {
		this.totalValorEmAtraso = totalValorEmAtraso;
	}
	public BigDecimal getTotalValorALiberar() {
		return totalValorALiberar;
	}
	public void setTotalValorALiberar(BigDecimal totalValorALiberar) {
		this.totalValorALiberar = totalValorALiberar;
	}
}
