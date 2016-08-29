package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;

public class ContasNPTVO {

	private Integer conta;
	private String autorizadasms;
	private Integer prontuario;
	private String nome; 
	private Date dtalta;
	private Date dtint;
	private Integer qtde;
	private Integer diferencadt;

	public Integer getConta() {
		return conta;
	}

	public void setConta(Integer conta) {
		this.conta = conta;
	}

	public String getAutorizadasms() {
		return autorizadasms;
	}

	public void setAutorizadasms(String autorizadasms) {
		this.autorizadasms = autorizadasms;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getDtalta() {
		return dtalta;
	}

	public void setDtalta(Date dtalta) {
		this.dtalta = dtalta;
	}

	public Date getDtint() {
		return dtint;
	}

	public void setDtint(Date dtint) {
		this.dtint = dtint;
	}

	public Integer getQtde() {
		return qtde;
	}

	public void setQtde(Integer qtde) {
		this.qtde = qtde;
	}

	public Integer getDiferencadt() {
		return diferencadt;
	}

	public void setDiferencadt(Integer diferencadt) {
		this.diferencadt = diferencadt;
	}


	public enum Fields {
		CONTA("conta"),
		AUTORIZADA_SMS("autorizadasms"),
		PRONTUARIO("prontuario"),
		DT_ALTA("dtalta"),
		DT_INT("dtint"),
		DIFERENCA_DT("diferencadt"),
		QTDE("qtde"),
		NOME("nome");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	
	
}
