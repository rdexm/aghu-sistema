package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

public class CurCutVO implements Serializable {

	private static final long serialVersionUID = 4687136712054953362L;
	
	private Integer qutSeq;
	private Short seqp;
	private Short ordemVisualizacao;
	private String sexoQuestao;
	private String textoFormatado;
	private Short fueSeq;
	private String descricao;
	
	public Integer getQutSeq() {
		return qutSeq;
	}
	
	public void setQutSeq(Integer qutSeq) {
		this.qutSeq = qutSeq;
	}
	
	public Short getSeqp() {
		return seqp;
	}
	
	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
	
	public Short getOrdemVisualizacao() {
		return ordemVisualizacao;
	}
	
	public void setOrdemVisualizacao(Short ordemVisualizacao) {
		this.ordemVisualizacao = ordemVisualizacao;
	}
	
	public String getSexoQuestao() {
		return sexoQuestao;
	}
	
	public void setSexoQuestao(String sexoQuestao) {
		this.sexoQuestao = sexoQuestao;
	}
	
	public String getTextoFormatado() {
		return textoFormatado;
	}
	
	public void setTextoFormatado(String textoFormatado) {
		this.textoFormatado = textoFormatado;
	}
	
	public Short getFueSeq() {
		return fueSeq;
	}
	
	public void setFueSeq(Short fueSeq) {
		this.fueSeq = fueSeq;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public enum Fields {

		QUT_SEQ("qutSeq"),
		SEQP("seqp"),
		ORDEM_VISUALIZACAO("ordemVisualizacao"),
		SEXO_QUESTAO("sexoQuestao"),
		TEXTO_FORMATADO("textoFormatado"),
		DESCRICAO("descricao"),
		FUE_SEQ("fueSeq");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
}