package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.io.Serializable;
import java.util.Date;

public class ProjetoPacientesVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3313936060641792027L;
	
	private Date dtInicio;
	private Date dtFim;
	private String nome;
	private String nomeResponsavel;
	
	public Date getDtInicio() {
		return dtInicio;
	}
	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}
	public Date getDtFim() {
		return dtFim;
	}
	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
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
