package br.gov.mec.aghu.indicadores.vo;

import java.math.BigDecimal;

public class UnidadeFuncionalIndicadoresVO {
	private Short unidade;
	private Short capacidade;
	private BigDecimal bloqueios; // zero
	private Integer blqDesat; // zero
	private Integer clinica;

	public Short getUnidade() {
		return unidade;
	}

	public void setUnidade(Short unidade) {
		this.unidade = unidade;
	}

	public Short getCapacidade() {
		return capacidade;
	}

	public void setCapacidade(Short capacidade) {
		this.capacidade = capacidade;
	}

	public BigDecimal getBloqueios() {
		return bloqueios;
	}

	public Integer getBlqDesat() {
		return blqDesat;
	}

	public void setBlqDesat(Integer blqDesat) {
		this.blqDesat = blqDesat;
	}

	public void setBloqueios(BigDecimal bloqueios) {
		this.bloqueios = bloqueios;
	}

	public Integer getClinica() {
		return clinica;
	}

	public void setClinica(Integer clinica) {
		this.clinica = clinica;
	}
}
