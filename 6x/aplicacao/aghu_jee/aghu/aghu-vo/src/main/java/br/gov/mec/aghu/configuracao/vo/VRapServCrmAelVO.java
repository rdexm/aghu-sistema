package br.gov.mec.aghu.configuracao.vo;

import java.io.Serializable;

public class VRapServCrmAelVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4003941208785053477L;
	
	private Integer matricula;
	private Short vinCodigo;
	private String nome;
	private String nomeUsual;
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
	public String getNomeUsual() {
		return nomeUsual;
	}
	public void setNomeUsual(String nomeUsual) {
		this.nomeUsual = nomeUsual;
	}
	public String getNroRegConselho() {
		return nroRegConselho;
	}
	public void setNroRegConselho(String nroRegConselho) {
		this.nroRegConselho = nroRegConselho;
	}

}
