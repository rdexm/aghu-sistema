
package br.gov.mec.aghu.exames.questionario.vo;

import java.io.Serializable;

public class QuestionarioVO implements Serializable {



	/**
	 * 
	 */
	private static final long serialVersionUID = -4993864810165946086L;
	private Integer seq;
	private String descricao;
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
