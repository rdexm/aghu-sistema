package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;


public class RelatorioControleChamadaPacienteVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7295144028720484566L;
	
	private String hrInicioCirurgia;//1
	private Short sala;//2		
	private String nome;//3
	private String quarto;;//4
	private Short numeroAgenda;//5
	private String origem;//6
	private Integer crgSeq;//7
	

	public RelatorioControleChamadaPacienteVO() {
		
	}
	
	
	//Getters and Setters

	public String getHrInicioCirurgia() {
		return hrInicioCirurgia;
	}


	public Short getSala() {
		return sala;
	}


	public String getNome() {
		return nome;
	}


	public String getQuarto() {
		return quarto;
	}


	public Short getNumeroAgenda() {
		return numeroAgenda;
	}


	public String getOrigem() {
		return origem;
	}


	public Integer getCrgSeq() {
		return crgSeq;
	}


	public void setHrInicioCirurgia(String hrInicioCirurgia) {
		this.hrInicioCirurgia = hrInicioCirurgia;
	}


	public void setSala(Short sala) {
		this.sala = sala;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public void setQuarto(String quarto) {
		this.quarto = quarto;
	}


	public void setNumeroAgenda(Short numeroAgenda) {
		this.numeroAgenda = numeroAgenda;
	}


	public void setOrigem(String origem) {
		this.origem = origem;
	}


	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}
	
	
}