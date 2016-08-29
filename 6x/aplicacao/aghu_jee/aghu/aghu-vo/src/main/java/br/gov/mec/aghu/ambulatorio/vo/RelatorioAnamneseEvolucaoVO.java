package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.Date;

public class RelatorioAnamneseEvolucaoVO implements Serializable {

	private static final long serialVersionUID = 8459574296812321792L;
	
	// Identificação
	private String nomePaciente; 
	private String prontuario; 
	private String infoConsulta;
	
	// Título (Anamnese ou Evolução)
	private String titulo;
	
	// Informações
	private String dados;
	private String notasAdicionais;
	
	//F_LOGO1	
	private String caminhoLogo;
	
	//Data
	private Date dtAtendimento;
	
	public String getCaminhoLogo() {
		return caminhoLogo;
	}
	public void setCaminhoLogo(String caminhoLogo) {
		this.caminhoLogo = caminhoLogo;
	}
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	public String getProntuario() {
		return prontuario;
	}
	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}
	public String getInfoConsulta() {
		return infoConsulta;
	}
	public void setInfoConsulta(String infoConsulta) {
		this.infoConsulta = infoConsulta;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getDados() {
		return dados;
	}
	public void setDados(String dados) {
		this.dados = dados;
	}
	public String getNotasAdicionais() {
		return notasAdicionais;
	}
	public void setNotasAdicionais(String notasAdicionais) {
		this.notasAdicionais = notasAdicionais;
	}
	public Date getDtAtendimento() {
		return dtAtendimento;
	}
	public void setDtAtendimento(Date dtAtendimento) {
		this.dtAtendimento = dtAtendimento;
	}
}
