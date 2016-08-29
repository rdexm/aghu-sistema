package br.gov.mec.aghu.prescricaomedica.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * VO para armazenar os dados de MpmPostoSaude
 * 
 * @author ihaas
 * 
 */
public class PostoSaude implements BaseBean {

	private static final long serialVersionUID = -4195817103912553934L;

	private Integer seq;
	private String descricao;
	private String cidade;

	public PostoSaude() {

	}

	public PostoSaude(Integer seq, String descricao) {
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

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
}