package br.gov.mec.aghu.internacao.vo;

import org.apache.commons.lang3.StringUtils;

public class ProcedenciaPacientesInternadosVO {
	
	String cidade;
	String prontuario;
	String nomePaciente;
	String leito;
	String unidade;
	String especialidade;
	String equipe;
	String nome;
	Short unfSeq;
	String dthrInicio;
	Long tempo;
	
	public String getCidade() {
		return cidade;
	}
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	public String getProntuario() {
		return prontuario;
	}
	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	public String getLeito() {
		return leito;
	}
	public void setLeito(String leito) {
		this.leito = leito;
	}
	public String getUnidade() {
		return unidade;
	}
	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}
	public String getEspecialidade() {
		return especialidade;
	}
	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}
	public String getEquipe() {
		
		if (StringUtils.isBlank(this.equipe) && StringUtils.isNotBlank(this.nome)) {
			
			if (nome.length() > 16) {
				
				this.equipe = this.nome.substring(0, 15);
			
			} else {
				
				this.equipe = nome;
				
			}
			
		}
		
		return this.equipe;
		
	}
	
	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}
	public Long getTempo() {
		return tempo;
	}
	public void setTempo(Long tempo) {
		this.tempo = tempo;
	}
	public Short getUnfSeq() {
		return unfSeq;
	}
	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}
	public String getDthrInicio() {
		return dthrInicio;
	}
	public void setDthrInicio(String dthrInicio) {
		this.dthrInicio = dthrInicio;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
}
