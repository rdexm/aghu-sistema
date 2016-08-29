package br.gov.mec.aghu.exames.pesquisa.vo;

import java.io.Serializable;


public class PesquisaExamesPacientesVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3985893960361163421L;
	private Integer codigo;
	private String nomePaciente;
	private String nomeSocialPaciente;
	private Integer prontuario;
	private Integer consulta;
	private Integer numeroSolicitacaoInfo;
	private Long protocolo;
	
	public Integer getCodigo() {
		return codigo;
	}
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public Integer getConsulta() {
		return consulta;
	}
	public void setConsulta(Integer consulta) {
		this.consulta = consulta;
	}
	public Integer getNumeroSolicitacaoInfo() {
		return numeroSolicitacaoInfo;
	}
	public void setNumeroSolicitacaoInfo(Integer numeroSolicitacaoInfo) {
		this.numeroSolicitacaoInfo = numeroSolicitacaoInfo;
	}
	public String getNomeSocialPaciente() {
		return nomeSocialPaciente;
	}
	public void setNomeSocialPaciente(String nomeSocialPaciente) {
		this.nomeSocialPaciente = nomeSocialPaciente;
	}
	public Long getProtocolo() {
		return protocolo;
	}
	public void setProtocolo(Long protocolo) {
		this.protocolo = protocolo;
	}	
	
}