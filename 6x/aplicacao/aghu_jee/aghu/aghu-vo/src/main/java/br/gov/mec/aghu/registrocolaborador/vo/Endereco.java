package br.gov.mec.aghu.registrocolaborador.vo;

import java.io.Serializable;


public class Endereco  implements Serializable {

	private static final long serialVersionUID = 518401481995666916L;
	
	private String logradouro;
	private String complLogradouro;
	private Integer nroLogradouro;
	private Integer cep;
	private String bairro;
	private String cidade;
	private String unidadeFederacao;
	private String pais;


	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getComplLogradouro() {
		return complLogradouro;
	}

	public void setComplLogradouro(String complLogradouro) {
		this.complLogradouro = complLogradouro;
	}

	public Integer getNroLogradouro() {
		return nroLogradouro;
	}

	public void setNroLogradouro(Integer nroLogradouro) {
		this.nroLogradouro = nroLogradouro;
	}

	public Integer getCep() {
		return cep;
	}

	public void setCep(Integer cep) {
		this.cep = cep;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getUnidadeFederacao() {
		return unidadeFederacao;
	}

	public void setUnidadeFederacao(String unidadeFederacao) {
		this.unidadeFederacao = unidadeFederacao;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	
}
