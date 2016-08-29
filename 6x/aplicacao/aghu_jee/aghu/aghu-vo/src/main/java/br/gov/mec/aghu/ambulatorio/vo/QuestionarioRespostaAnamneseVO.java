package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

public class QuestionarioRespostaAnamneseVO implements Serializable{

	private static final long serialVersionUID = -98454728018368L;
	
	private String valorValidoQuestao;
	private String textoAntesResposta;
	private String textoDepoisResposta;
	private String textoFormatado;
	private String descricao;
	private Short ordemVisualizacao;
	private String resposta;
	private Integer qutSeq;
	private Short seqpQuestao;
	
	public enum Fields {
		VALOR_VALIDO_QUESTAO("valorValidoQuestao"),
		TEXTO_ANTES_RESPOSTA("textoAntesResposta"),
		TEXTO_DEPOIS_RESPOSTA("textoDepoisResposta"),
		TEXTO_FORMATADO("textoFormatado"),
		DESCRICAO("descricao"),
		ORDEM_VISUALIZACAO("ordemVisualizacao"),
		RESPOSTA("resposta"),
		QUT_SEQ("qutSeq"),
		SEQP_QUESTAO("seqpQuestao");
		
		private String field;
		
		private Fields(String field) {
			this.field = field;
		}
		
		@Override
		public String toString() {
			return this.field;
		}
	}

	
	public String getValorValidoQuestao() {
		return valorValidoQuestao;
	}
	
	public void setValorValidoQuestao(String valorValidoQuestao) {
		this.valorValidoQuestao = valorValidoQuestao;
	}
	
	public String getTextoAntesResposta() {
		return textoAntesResposta;
	}
	
	public void setTextoAntesResposta(String textoAntesResposta) {
		this.textoAntesResposta = textoAntesResposta;
	}
	
	public String getTextoDepoisResposta() {
		return textoDepoisResposta;
	}
	
	public void setTextoDepoisResposta(String textoDepoisResposta) {
		this.textoDepoisResposta = textoDepoisResposta;
	}
	
	public String getTextoFormatado() {
		return textoFormatado;
	}
	
	public void setTextoFormatado(String textoFormatado) {
		this.textoFormatado = textoFormatado;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public Short getOrdemVisualizacao() {
		return ordemVisualizacao;
	}
	
	public void setOrdemVisualizacao(Short ordemVisualizacao) {
		this.ordemVisualizacao = ordemVisualizacao;
	}
	
	public String getResposta() {
		return resposta;
	}
	
	public void setResposta(String resposta) {
		this.resposta = resposta;
	}
	
	public Integer getQutSeq() {
		return qutSeq;
	}
	
	public void setQutSeq(Integer qutSeq) {
		this.qutSeq = qutSeq;
	}
	
	public Short getSeqpQuestao() {
		return seqpQuestao;
	}
	
	public void setSeqpQuestao(Short seqpQuestao) {
		this.seqpQuestao = seqpQuestao;
	}
}
