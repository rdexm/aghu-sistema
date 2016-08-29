package br.gov.mec.aghu.internacao.vo;


public class ProfessorVO {

	private String nomeUsual = null;
	private String nroRegConselho = null;

	public void setNroRegConselho(String nroRegConselho) {
		this.nroRegConselho = nroRegConselho;
	}

	public String getNroRegConselho() {
		return this.nroRegConselho;
	}

	public void setNomeUsual(String nomeUsual) {
		this.nomeUsual = nomeUsual;
	}

	public String getNomeUsual() {
		return this.nomeUsual;
	}

}