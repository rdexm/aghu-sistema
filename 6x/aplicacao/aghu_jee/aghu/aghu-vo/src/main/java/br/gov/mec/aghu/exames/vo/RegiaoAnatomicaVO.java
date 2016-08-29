package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;

public class RegiaoAnatomicaVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2926144697995547241L;
	
	private Integer seq;
	private String descricao;
	
	public RegiaoAnatomicaVO(Integer seq, String descricao) {
		super();
		this.seq = seq;
		this.descricao = descricao;
	}
	
	public RegiaoAnatomicaVO(){}
	
	public String getDescricao() {
		return descricao;
	}
	public Integer getSeq() {
		return seq;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}	

}
