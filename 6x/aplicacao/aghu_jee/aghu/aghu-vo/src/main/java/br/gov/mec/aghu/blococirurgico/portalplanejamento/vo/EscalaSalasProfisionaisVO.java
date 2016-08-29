package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.io.Serializable;

public class EscalaSalasProfisionaisVO implements Serializable {
	private static final long	serialVersionUID	= -1236052478840941161L;
	private String				siglaEspecialidade;
	private String				nome;

	public void setSiglaEspecialidade(String siglaEspecialidade) {
		this.siglaEspecialidade = siglaEspecialidade;
	}

	public String getSiglaEspecialidade() {
		return siglaEspecialidade;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

}
