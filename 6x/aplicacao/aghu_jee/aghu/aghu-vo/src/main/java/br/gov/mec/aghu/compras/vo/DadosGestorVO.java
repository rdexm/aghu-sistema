package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;

public class DadosGestorVO implements Serializable {

	private static final long serialVersionUID = 1094758748075863219L;
	
	private String nomeGestor;
	private Integer matriculaGestor;
	private String mlcCodigo;
	
	public String getNomeGestor() {
		return nomeGestor;
	}
	public void setNomeGestor(String nomeGestor) {
		this.nomeGestor = nomeGestor;
	}
	public Integer getMatriculaGestor() {
		return matriculaGestor;
	}
	public void setMatriculaGestor(Integer matriculaGestor) {
		this.matriculaGestor = matriculaGestor;
	}
	public String getMlcCodigo() {
		return mlcCodigo;
	}
	public void setMlcCodigo(String mlcCodigo) {
		this.mlcCodigo = mlcCodigo;
	}

}
