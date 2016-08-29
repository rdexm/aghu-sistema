package br.gov.mec.aghu.prescricaoenfermagem.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class RelatorioEvolucoesPacienteVO implements Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = -3314048937268755225L;
	private String nomePaciente;
	private String nomeMaePaciente;
	private String idade;
	private String prontuario;
	private String especialidade;
	private String unidade;
	private String equipe;
	private Date referencia;
	private List<RelatorioEvolucoesNotasAdicionaisPacienteVO> evolucoes;

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getNomeMaePaciente() {
		return nomeMaePaciente;
	}

	public void setNomeMaePaciente(String nomeMaePaciente) {
		this.nomeMaePaciente = nomeMaePaciente;
	}

	public String getIdade() {
		return idade;
	}

	public void setIdade(String idade) {
		this.idade = idade;
	}

	public List<RelatorioEvolucoesNotasAdicionaisPacienteVO> getEvolucoes() {
		return evolucoes;
	}

	public void setEvolucoes(
			List<RelatorioEvolucoesNotasAdicionaisPacienteVO> evolucoes) {
		this.evolucoes = evolucoes;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public String getEquipe() {
		return equipe;
	}

	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}

	public Date getReferencia() {
		return referencia;
	}

	public void setReferencia(Date referencia) {
		this.referencia = referencia;
	}

}
