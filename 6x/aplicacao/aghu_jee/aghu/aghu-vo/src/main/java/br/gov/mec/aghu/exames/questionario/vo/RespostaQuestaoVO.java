
package br.gov.mec.aghu.exames.questionario.vo;

import java.io.Serializable;

public class RespostaQuestaoVO implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7376996498223939299L;
	/**
	 * 
	 */
	private String grupo;
	private String questao;
	private String resposta;
	private Integer qtnSeq;
	private Integer qaoSeq;
	private String descricao;
	
	public String getGrupo() {
		return grupo;
	}
	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}
	public String getQuestao() {
		return questao;
	}
	public void setQuestao(String questao) {
		this.questao = questao;
	}
	public String getResposta() {
		return resposta;
	}
	public void setResposta(String resposta) {
		this.resposta = resposta;
	}
	public Integer getQtnSeq() {
		return qtnSeq;
	}
	public void setQtnSeq(Integer qtnSeq) {
		this.qtnSeq = qtnSeq;
	}
	public Integer getQaoSeq() {
		return qaoSeq;
	}
	public void setQaoSeq(Integer qaoSeq) {
		this.qaoSeq = qaoSeq;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	
}
