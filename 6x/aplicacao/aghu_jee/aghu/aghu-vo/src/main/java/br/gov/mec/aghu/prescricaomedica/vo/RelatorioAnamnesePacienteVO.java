package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;

import java.util.Date;

import java.util.List;

public class RelatorioAnamnesePacienteVO implements Serializable {

	/**

     * 

     */

	private static final long serialVersionUID = 1214404825469601373L;

	private String nomePaciente;

	private String idade;

	private String prontuario;

	private String especialidade;

	private String unidade;

	private String equipe;

	private Date referencia;

	private List<RelatorioAnamneseNotasAdicionaisPacienteVO> anamneseNotasAdicionais;

	public String getNomePaciente() {

		return nomePaciente;

	}

	public void setNomePaciente(String nomePaciente) {

		this.nomePaciente = nomePaciente;

	}

	public String getIdade() {

		return idade;

	}

	public void setIdade(String idade) {

		this.idade = idade;

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

	public List<RelatorioAnamneseNotasAdicionaisPacienteVO> getAnamneseNotasAdicionais() {

		return anamneseNotasAdicionais;

	}

	public void setAnamneseNotasAdicionais(

	List<RelatorioAnamneseNotasAdicionaisPacienteVO> anamneseNotasAdicionais) {

		this.anamneseNotasAdicionais = anamneseNotasAdicionais;

	}

	public Date getReferencia() {

		return referencia;

	}

	public void setReferencia(Date referencia) {

		this.referencia = referencia;

	}

}
