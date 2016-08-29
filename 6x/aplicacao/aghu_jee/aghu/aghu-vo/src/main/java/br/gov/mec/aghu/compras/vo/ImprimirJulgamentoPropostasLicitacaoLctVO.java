package br.gov.mec.aghu.compras.vo;

import java.util.Date;

public class ImprimirJulgamentoPropostasLicitacaoLctVO {
	
	private String lctDescricao; //1
	private Integer lctNumero; //2
	private Date lctData; //3
	private Integer lctHora; //4
	
	public String getLctDescricao() {
		return lctDescricao;
	}
	public void setLctDescricao(String lctDescricao) {
		this.lctDescricao = lctDescricao;
	}
	public Integer getLctNumero() {
		return lctNumero;
	}
	public void setLctNumero(Integer lctNumero) {
		this.lctNumero = lctNumero;
	}
	
	public Integer getLctHora() {
		return lctHora;
	}
	public void setLctHora(Integer lctHora) {
		this.lctHora = lctHora;
	}
	public Date getLctData() {
		return lctData;
	}
	public void setLctData(Date lctData) {
		this.lctData = lctData;
	}
}
