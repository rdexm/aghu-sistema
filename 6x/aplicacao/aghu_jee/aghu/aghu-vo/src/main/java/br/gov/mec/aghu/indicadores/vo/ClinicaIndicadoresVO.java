package br.gov.mec.aghu.indicadores.vo;

import java.math.BigDecimal;

public class ClinicaIndicadoresVO {
	private Integer clinica;
	private Integer capacidade;
	private BigDecimal bloqueios;
	private Integer blqDesat;
	private Integer capcMais;
	private Integer capcMenos;
	
	public Integer getClinica() {
		return clinica;
	}
	public void setClinica(Integer clinica) {
		this.clinica = clinica;
	}
	public Integer getCapacidade() {
		return capacidade;
	}
	public void setCapacidade(Integer capacidade) {
		this.capacidade = capacidade;
	}
	public BigDecimal getBloqueios() {
		return bloqueios;
	}
	public void setBloqueios(BigDecimal bloqueios) {
		this.bloqueios = bloqueios;
	}
	public Integer getBlqDesat() {
		return blqDesat;
	}
	public void setBlqDesat(Integer blqDesat) {
		this.blqDesat = blqDesat;
	}
	public Integer getCapcMais() {
		return capcMais;
	}
	public void setCapcMais(Integer capcMais) {
		this.capcMais = capcMais;
	}
	public Integer getCapcMenos() {
		return capcMenos;
	}
	public void setCapcMenos(Integer capcMenos) {
		this.capcMenos = capcMenos;
	}
}
