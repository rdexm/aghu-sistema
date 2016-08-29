package br.gov.mec.aghu.emergencia.vo;

import java.io.Serializable;

public class IndicacaoNascimentoVO implements Serializable {

	private static final long serialVersionUID = 6283159014461390969L;
	private Integer seq;
	private String descricao;
	
	public IndicacaoNascimentoVO() {

	}
	public IndicacaoNascimentoVO(Integer seq, String descricao) {
		this.seq = seq;
		this.descricao = descricao;
	}
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
}
