package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;

public class EquipamentoProcessamentoMensalVO {

	private Integer codCentroCusto;
	private String bem;
	private BigDecimal totalDepreciado;
	
	public EquipamentoProcessamentoMensalVO(){
	}
	
	public BigDecimal getTotalDepreciado() {
		return totalDepreciado;
	}
	
	public void setTotalDepreciado(BigDecimal totalDepreciado) {
		this.totalDepreciado = totalDepreciado;
	}
	
	public String getBem() {
		return bem;
	}
	
	public void setBem(String bem) {
		this.bem = bem;
	}
	
	public Integer getCodCentroCusto() {
		return codCentroCusto;
	}

	public void setCodCentroCusto(Integer codCentroCusto) {
		this.codCentroCusto = codCentroCusto;
	}
	
}