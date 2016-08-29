package br.gov.mec.aghu.registrocolaborador.vo;

import java.io.Serializable;

public class Servidor implements Serializable {
	private static final long serialVersionUID = -1220099151938980779L;
	
	private Short vinculo;
	private Integer matricula;
	private String indSituacao;
	private String nomePessoaFisica;
	private Integer codigoPessoaFisica;
	private String nomeUsual;

	public String getMatriculaVinculo() {
		return this.getVinculo() + " " + this.getMatricula();
	}

	public Short getVinculo() {
		return vinculo;
	}

	public void setVinculo(Short vinculo) {
		this.vinculo = vinculo;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public String getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	public String getNomePessoaFisica() {
		return nomePessoaFisica;
	}

	public void setNomePessoaFisica(String nomePessoaFisica) {
		this.nomePessoaFisica = nomePessoaFisica;
	}

	public Integer getCodigoPessoaFisica() {
		return codigoPessoaFisica;
	}

	public void setCodigoPessoaFisica(Integer codigoPessoaFisica) {
		this.codigoPessoaFisica = codigoPessoaFisica;
	}

	public String getNomeUsual() {
		return nomeUsual;
	}

	public void setNomeUsual(String nomeUsual) {
		this.nomeUsual = nomeUsual;
	}
	
	public String getDescricao() {
		StringBuilder resultado = new StringBuilder(vinculo);
		if(matricula != null) {
			resultado.append('-').append(matricula);
		}
		if(nomePessoaFisica != null && !"".equals(nomePessoaFisica)) {
			resultado.append('-').append(nomePessoaFisica);
		}
		if(nomeUsual != null && !"".equals(nomeUsual)) {
			resultado.append('-').append(nomeUsual);
		}
		return resultado.toString();
	}
}
