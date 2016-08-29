package br.gov.mec.aghu.internacao.vo;

import java.util.Date;

/**
 * Classe responsável por agrupar informações a serem exibidos no relatório
 * de baixas por dia
 * 
 * @author tfelini
 * 
 */

public class BaixasDiaVO {
	
	private String origem;
	private String grupoConvenio;
	private String prontuario;
	private String nomePaciente;
	private Date dataIngresso;
	private String local;
	private String siglaEspecialidade;
	private String caraterInternacao;
	private String crmResponsavel;
	private String nomeResponsavel;
	private Date dataNascimento;
	private String dc;
	private Long codFat;
	private String senha;
	
	
	public String getOrigem() {
		return origem;
	}
	public void setOrigem(String origem) {
		this.origem = origem;
	}
	public String getGrupoConvenio() {
		return grupoConvenio;
	}
	public void setGrupoConvenio(String grupoConvenio) {
		this.grupoConvenio = grupoConvenio;
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
	public Date getDataIngresso() {
		return dataIngresso;
	}
	public void setDataIngresso(Date dataIngresso) {
		this.dataIngresso = dataIngresso;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public String getSiglaEspecialidade() {
		return siglaEspecialidade;
	}
	public void setSiglaEspecialidade(String siglaEspecialidade) {
		this.siglaEspecialidade = siglaEspecialidade;
	}
	public String getCaraterInternacao() {
		return caraterInternacao;
	}
	public void setCaraterInternacao(String caraterInternacao) {
		this.caraterInternacao = caraterInternacao;
	}
	public String getCrmResponsavel() {
		return crmResponsavel;
	}
	public void setCrmResponsavel(String crmResponsavel) {
		this.crmResponsavel = crmResponsavel;
	}
	public String getNomeResponsavel() {
		return nomeResponsavel;
	}
	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}
	public Date getDataNascimento() {
		return dataNascimento;
	}
	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	public String getDc() {
		return dc;
	}
	public void setDc(String dc) {
		this.dc = dc;
	}
	public Long getCodFat() {
		return codFat;
	}
	public void setCodFat(Long codFat) {
		this.codFat = codFat;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	
}
