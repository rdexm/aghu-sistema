package br.gov.mec.aghu.faturamento.vo;

import java.math.BigDecimal;

public class ItemProcedimentoHospitalarVO {

	private String descricao;
	private BigDecimal totalServHosp;
	private BigDecimal totalServProf;
	private BigDecimal totalAnest;
	private BigDecimal totalProcedimento;
	private BigDecimal totalSADT;
	private BigDecimal valor;
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public BigDecimal getTotalServHosp() {
		return totalServHosp;
	}

	public void setTotalServHosp(BigDecimal totalServHosp) {
		this.totalServHosp = totalServHosp;
	}

	public BigDecimal getTotalServProf() {
		return totalServProf;
	}

	public void setTotalServProf(BigDecimal totalServProf) {
		this.totalServProf = totalServProf;
	}

	public BigDecimal getTotalAnest() {
		return totalAnest;
	}

	public void setTotalAnest(BigDecimal totalAnest) {
		this.totalAnest = totalAnest;
	}

	public BigDecimal getTotalProcedimento() {
		return totalProcedimento;
	}

	public void setTotalProcedimento(BigDecimal totalProcedimento) {
		this.totalProcedimento = totalProcedimento;
	}

	public BigDecimal getTotalSADT() {
		return totalSADT;
	}

	public void setTotalSADT(BigDecimal totalSADT) {
		this.totalSADT = totalSADT;
	}

	public BigDecimal getValor() {
		valor = BigDecimal.ZERO;
		
		if(totalServHosp!=null) {
			valor = valor.add(totalServHosp); 
		} 
		if(totalServProf!=null) {
			valor = valor.add(totalServProf); 
		} 

		if(totalAnest!=null) {
			valor = valor.add(totalAnest); 
		} 

		if(totalProcedimento!=null) {
			valor = valor.add(totalProcedimento); 
		} 

		if(totalSADT!=null) {
			valor = valor.add(totalSADT); 
		} 

		return valor;
	}
	
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}	
}
