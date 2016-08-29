package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.io.Serializable;
import java.util.Date;


public class NotasPolVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6317377482604171703L;
	
	private String cabecalho;			// Campo 1
	private String descricao;			// Campo 2
	private String assinado;			// Campo 3
	//private String logo;				// Campo 4
	private String nomePaciente;		// Campo 5
	private String prontuarioPaciente;	// Campo 6
	private Integer seqNotaAdicional;	// Campo 7
	private Date dataHora;				// Campo 8

	
	public String getCabecalho() {
		return cabecalho;
	}
	public void setCabecalho(String cabecalho) {
		this.cabecalho = cabecalho;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getAssinado() {
		return assinado;
	}
	public void setAssinado(String assinado) {
		this.assinado = assinado;
	}
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	public String getProntuarioPaciente() {
		return prontuarioPaciente;
	}
	public void setProntuarioPaciente(String prontuarioPaciente) {
		this.prontuarioPaciente = prontuarioPaciente;
	}
	public Integer getSeqNotaAdicional() {
		return seqNotaAdicional;
	}
	public void setSeqNotaAdicional(Integer seqNotaAdicional) {
		this.seqNotaAdicional = seqNotaAdicional;
	}
	public Date getDataHora() {
		return dataHora;
	}
	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}
}
