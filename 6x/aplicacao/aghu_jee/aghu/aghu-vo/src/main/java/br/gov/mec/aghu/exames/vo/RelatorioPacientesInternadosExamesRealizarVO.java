package br.gov.mec.aghu.exames.vo;

import java.util.List;

public class RelatorioPacientesInternadosExamesRealizarVO {

	public List<RelatorioPacientesInternadosExamesRealizarDadosVO> jejumNpo;
	public List<RelatorioPacientesInternadosExamesRealizarDadosVO> preparo;
	public List<RelatorioPacientesInternadosExamesRealizarDadosVO> dietaDiferenciada;
	public List<RelatorioPacientesInternadosExamesRealizarDadosVO> unidadeEmergencia;
	public List<RelatorioPacientesInternadosExamesRealizarDadosVO> todos;
	
	
	public List<RelatorioPacientesInternadosExamesRealizarDadosVO> getJejumNpo() {
		return jejumNpo;
	}
	public void setJejumNpo(
			List<RelatorioPacientesInternadosExamesRealizarDadosVO> jejumNpo) {
		this.jejumNpo = jejumNpo;
	}
	public List<RelatorioPacientesInternadosExamesRealizarDadosVO> getPreparo() {
		return preparo;
	}
	public void setPreparo(
			List<RelatorioPacientesInternadosExamesRealizarDadosVO> preparo) {
		this.preparo = preparo;
	}
	public List<RelatorioPacientesInternadosExamesRealizarDadosVO> getDietaDiferenciada() {
		return dietaDiferenciada;
	}
	public void setDietaDiferenciada(
			List<RelatorioPacientesInternadosExamesRealizarDadosVO> dietaDiferenciada) {
		this.dietaDiferenciada = dietaDiferenciada;
	}
	public List<RelatorioPacientesInternadosExamesRealizarDadosVO> getUnidadeEmergencia() {
		return unidadeEmergencia;
	}
	public void setUnidadeEmergencia(
			List<RelatorioPacientesInternadosExamesRealizarDadosVO> unidadeEmergencia) {
		this.unidadeEmergencia = unidadeEmergencia;
	}
	public List<RelatorioPacientesInternadosExamesRealizarDadosVO> getTodos() {
		return todos;
	}
	public void setTodos(
			List<RelatorioPacientesInternadosExamesRealizarDadosVO> todos) {
		this.todos = todos;
	}

}
