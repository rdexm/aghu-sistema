package br.gov.mec.aghu.internacao.vo;

import java.io.Serializable;


public class ServidorConselhoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3177035447534137511L;

	private Integer matricula;

	private Short vinCodigo;

	private String nome;

	private String nroRegConselho;

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}
	
	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNroRegConselho() {
		return nroRegConselho;
	}

	public void setNroRegConselho(String nroRegConselho) {
		this.nroRegConselho = nroRegConselho;
	}
	
}
