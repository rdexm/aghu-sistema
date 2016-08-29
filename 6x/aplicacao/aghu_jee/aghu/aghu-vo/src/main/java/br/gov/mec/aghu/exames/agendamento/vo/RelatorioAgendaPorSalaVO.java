package br.gov.mec.aghu.exames.agendamento.vo;

import java.io.Serializable;
import java.util.Date;

public class RelatorioAgendaPorSalaVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2533140758354427377L;
	
	private String salaNumero;
	private String nomeSala;
	private Date hora;
	private String sala;
	private Integer prontuario;
	private String nomePaciente;
	private Integer idade;
	private String localizacao;
	private Integer solicitacao;
	private String descricaoExame;
	private Boolean extra;
	private Integer atdSeq;
	private Date dataNascimento;
	
	public RelatorioAgendaPorSalaVO(){
	}
	
	public RelatorioAgendaPorSalaVO(String salaNumero, String nomeSala,
			Date hora, String sala, Integer prontuario, String nomePaciente,
			Date dataNascimento, Integer atdSeq, Integer solicitacao,
			String descricaoExame, Boolean extra) {
		super();
		this.salaNumero = salaNumero;
		this.nomeSala = nomeSala;
		this.hora = hora;
		this.sala = sala;
		this.prontuario = prontuario;
		this.nomePaciente = nomePaciente;
		this.dataNascimento = dataNascimento;
		this.atdSeq = atdSeq;
		this.solicitacao = solicitacao;
		this.descricaoExame = descricaoExame;
		this.extra = extra;
	}
	
	public String getSala() {
		return sala;
	}
	
	public void setSala(String sala) {
		this.sala = sala;
	}
	
	public void setNomeSala(String nomeSala) {
		this.nomeSala = nomeSala;
	}
	
	public String getNomeSala() {
		return nomeSala;
	}

	public String getSalaNumero() {
		return salaNumero;
	}

	public void setSalaNumero(String salaNumero) {
		this.salaNumero = salaNumero;
	}

	public Date getHora() {
		return hora;
	}

	public void setHora(Date hora) {
		this.hora = hora;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Integer getIdade() {
		return idade;
	}

	public void setIdade(Integer idade) {
		this.idade = idade;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}

	public Integer getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(Integer solicitacao) {
		this.solicitacao = solicitacao;
	}

	public String getDescricaoExame() {
		return descricaoExame;
	}

	public void setDescricaoExame(String descricaoExame) {
		this.descricaoExame = descricaoExame;
	}

	public Boolean getExtra() {
		return extra;
	}

	public void setExtra(Boolean extra) {
		this.extra = extra;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}
}