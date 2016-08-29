package br.gov.mec.aghu.administracao.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * VO para armazenar os dados de AghMicrocomputador
 * 
 * @author ihaas
 * 
 */
public class MicroComputador implements BaseBean {

	private static final long serialVersionUID = -4195817103912553934L;

	private Short unfSeq;
	private String nome;

	public MicroComputador() {

	}

	public MicroComputador(Short unfSeq, String nome) {
		this.unfSeq = unfSeq;
		this.nome = nome;
	}
	

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

}