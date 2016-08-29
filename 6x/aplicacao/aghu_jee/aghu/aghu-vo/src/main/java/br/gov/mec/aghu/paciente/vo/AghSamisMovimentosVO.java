package br.gov.mec.aghu.paciente.vo;

import java.util.Date;

public class AghSamisMovimentosVO {
	
	private Integer codigoMovimentacao;
	private Integer prontuario;
	private Integer pacCodigo;
	private String nomePaciente;
	private Date dataMovimentacao;
	private Date dataCadastroOrigemProntuario;
	private String origemProntuario;
	private String localAtual;
	
	private Boolean selecionado;

	
	public AghSamisMovimentosVO() {
		this.selecionado = false;
	}
	

	public Integer getCodigoMovimentacao() {
		return codigoMovimentacao;
	}


	public void setCodigoMovimentacao(Integer codigoMovimentacao) {
		this.codigoMovimentacao = codigoMovimentacao;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Date getDataMovimentacao() {
		return dataMovimentacao;
	}

	public void setDataMovimentacao(Date dataMovimentacao) {
		this.dataMovimentacao = dataMovimentacao;
	}

	public String getOrigemProntuario() {
		return origemProntuario;
	}

	public void setOrigemProntuario(String origemProntuario) {
		this.origemProntuario = origemProntuario;
	}

	public String getLocalAtual() {
		return localAtual;
	}

	public void setLocalAtual(String localAtual) {
		this.localAtual = localAtual;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	public Date getDataCadastroOrigemProntuario() {
		return dataCadastroOrigemProntuario;
	}

	public void setDataCadastroOrigemProntuario(Date dataCadastroOrigemProntuario) {
		this.dataCadastroOrigemProntuario = dataCadastroOrigemProntuario;
	}
	
}
