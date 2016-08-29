package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class RelatorioProgramacaoGradeHorarioVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8842938503408361477L;
	
	/*Campos retornados pela consulta C2 (#6810) */
	private String diaSemana;
	private String tpsTipo;
	private String caaSigla;
	private String dia;
	private String hora;
	private BigDecimal quantidade;

	
	/*Campos concatenados*/
	private String tipo;
	
	public String getTpsTipo() {
		return tpsTipo;
	}
	public void setTpsTipo(String tpsTipo) {
		this.tpsTipo = tpsTipo;
	}
	public String getCaaSigla() {
		return caaSigla;
	}
	public void setCaaSigla(String caaSigla) {
		this.caaSigla = caaSigla;
	}
	public String getDia() {
		return dia;
	}
	public void setDia(String dia) {
		this.dia = dia;
	}
	public String getHora() {
		return hora;
	}
	public void setHora(String hora) {
		this.hora = hora;
	}
	public BigDecimal getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(BigDecimal quantidade) {
		this.quantidade = quantidade;
	}
	public String getDiaSemana() {
		return diaSemana;
	}
	public void setDiaSemana(String diaSemana) {
		this.diaSemana = diaSemana;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
}
