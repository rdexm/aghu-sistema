package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.AelProjetoPacientes;


public class ProjetoPesquisaVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6249844109894226392L;

	AelProjetoPacientes projetoPesquisa;
	
	private String nome;
	
	private String nomeResponsavel;

	public AelProjetoPacientes getProjetoPesquisa() {
		return projetoPesquisa;
	}

	public void setProjetoPesquisa(AelProjetoPacientes projetoPesquisa) {
		this.projetoPesquisa = projetoPesquisa;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomeResponsavel() {
		return nomeResponsavel;
	}

	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}
	
	
}
