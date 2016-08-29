package br.gov.mec.aghu.internacao.vo;

import java.io.Serializable;

/**
 * Filtro para busca de usuário através do web service.
 * 
 * @author Flavio Rutkowski
 * 
 */
public class UnidadeFuncional implements Serializable {

	private static final long serialVersionUID = -4195817103912553934L;

	private Short seq;
	private String descricao;
	private String sigla;

	public UnidadeFuncional() {

	}

	public UnidadeFuncional(Short seq, String descricao, String sigla) {
		this.seq = seq;
		this.descricao = descricao;
		this.sigla = sigla;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

}
