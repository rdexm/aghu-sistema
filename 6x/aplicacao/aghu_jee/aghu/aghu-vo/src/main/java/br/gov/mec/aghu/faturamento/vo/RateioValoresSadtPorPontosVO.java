package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;

public class RateioValoresSadtPorPontosVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2647423891671520347L;

	private Short seq;
	
	private String descricao;
	
	private Long pontosSadt;
	
	public RateioValoresSadtPorPontosVO(Short seq, String descricao, Long pontosSadt) {
		this.seq = seq;
		this.descricao = descricao;
		this.pontosSadt = pontosSadt;
	}
	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Long getPontosSadt() {
		return pontosSadt;
	}

	public void setPontosSadt(Long pontosSadt) {
		this.pontosSadt = pontosSadt;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}
}
