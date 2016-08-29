package br.gov.mec.aghu.emergencia.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioSituacao;

public class DiagnosticoFiltro implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3719423092534467385L;

	private Integer seq;
	private String descricao;
	private DominioSituacao indSituacao;
	private Boolean indPlacar;


	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Boolean getIndPlacar() {
		return indPlacar;
	}

	public void setIndPlacar(Boolean indPlacar) {
		this.indPlacar = indPlacar;
	}



}
