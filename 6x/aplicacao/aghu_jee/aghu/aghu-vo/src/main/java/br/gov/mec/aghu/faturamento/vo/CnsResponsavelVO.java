package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;


public class CnsResponsavelVO implements Serializable {

	private static final long serialVersionUID = -4392595908304672411L;
	
	private Integer matricula;
	
	private Short vinCodigo;
	
	private Long itemCns;

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public Long getItemCns() {
		return itemCns;
	}

	public void setItemCns(Long itemCns) {
		this.itemCns = itemCns;
	}
	
	
}