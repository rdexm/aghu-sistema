package br.gov.mec.aghu.compras.pac.vo;

import java.util.Date;

public class RelatorioApVO {
	
	private Integer processo;
	private Date geracao;
	private String modalidade;
	private String localizacao;
	private Short cod;
	private Date dtEntrada;
	private Date dtSaida;
	
	public enum Fields {

		PROCESSO("processo"),
		GERACAO("geracao"),
		MODALIDADE("modalidade"),
		LOCALIZACAO("localizacao"),
		CODIGO("cod"),
		DT_ENTRADA("dtEntrada"),
		DT_SAIDA("dtSaida");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
	
	public Integer getProcesso() {
		return processo;
	}
	public void setProcesso(Integer processo) {
		this.processo = processo;
	}
	public Date getGeracao() {
		return geracao;
	}
	public void setGeracao(Date geracao) {
		this.geracao = geracao;
	}
	public String getModalidade() {
		return modalidade;
	}
	public void setModalidade(String modalidade) {
		this.modalidade = modalidade;
	}
	public String getLocalizacao() {
		return localizacao;
	}
	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}
	public Short getCod() {
		return cod;
	}
	public void setCod(Short cod) {
		this.cod = cod;
	}
	public Date getDtEntrada() {
		return dtEntrada;
	}
	public void setDtEntrada(Date dtEntrada) {
		this.dtEntrada = dtEntrada;
	}
	public Date getDtSaida() {
		return dtSaida;
	}
	public void setDtSaida(Date dtSaida) {
		this.dtSaida = dtSaida;
	}
	
}
