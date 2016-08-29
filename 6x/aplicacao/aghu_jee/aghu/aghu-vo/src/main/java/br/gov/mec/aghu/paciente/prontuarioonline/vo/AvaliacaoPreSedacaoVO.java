package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioAsa;



public class AvaliacaoPreSedacaoVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5486568805180953497L;
	
	private String descViaAereas;
	private DominioAsa asa;
	private String avaliacaoClinica;
	private String comorbidades;
	private String exameFisico;
	private String nroRegConselho;	
	private String nome;
	public String getDescViaAereas() {
		return descViaAereas;
	}
	public void setDescViaAereas(String descViaAereas) {
		this.descViaAereas = descViaAereas;
	}
	public DominioAsa getAsa() {
		return asa;
	}
	public void setAsa(DominioAsa asa) {
		this.asa = asa;
	}
	public String getAvaliacaoClinica() {
		return avaliacaoClinica;
	}
	public void setAvaliacaoClinica(String avaliacaoClinica) {
		this.avaliacaoClinica = avaliacaoClinica;
	}
	public String getComorbidades() {
		return comorbidades;
	}
	public void setComorbidades(String comorbidades) {
		this.comorbidades = comorbidades;
	}
	public String getExameFisico() {
		return exameFisico;
	}
	public void setExameFisico(String exameFisico) {
		this.exameFisico = exameFisico;
	}
	public String getNroRegConselho() {
		return nroRegConselho;
	}
	public void setNroRegConselho(String nroRegConselho) {
		this.nroRegConselho = nroRegConselho;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
		
}
