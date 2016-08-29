package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

public class PreGeraItemQuestVO implements Serializable {

	private static final long serialVersionUID = -1213942147615722980L;

	private String pergunta;
	private String resposta;
	private Short ordem;
	private Integer qusQutSeq;
	private Short qusSeqP;
	private Short seqP;
	private Long pEvoSeq;
	private Short vEspSeq;
	
	private boolean renderCheck;
	private boolean checkValor;
	private String valor;

	private Integer vvqQusQutSeq;
	private Short vvqQusSeqP;
	private Short vvqSeqP;
	
	public Short getOrdem() {
		return ordem;
	}
	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}
	public String getPergunta() {
		return pergunta;
	}
	public void setPergunta(String pergunta) {
		this.pergunta = pergunta;
	}
	public String getResposta() {
		return resposta;
	}
	public void setResposta(String resposta) {
		this.resposta = resposta;
	}
	public Integer getQusQutSeq() {
		return qusQutSeq;
	}
	public void setQusQutSeq(Integer qusQutSeq) {
		this.qusQutSeq = qusQutSeq;
	}
	public Short getQusSeqP() {
		return qusSeqP;
	}
	public void setQusSeqP(Short qusSeqP) {
		this.qusSeqP = qusSeqP;
	}
	public Short getSeqP() {
		return seqP;
	}
	public void setSeqP(Short seqP) {
		this.seqP = seqP;
	}
	public Long getpEvoSeq() {
		return pEvoSeq;
	}
	public void setpEvoSeq(Long pEvoSeq) {
		this.pEvoSeq = pEvoSeq;
	}
	public Short getvEspSeq() {
		return vEspSeq;
	}
	public void setvEspSeq(Short vEspSeq) {
		this.vEspSeq = vEspSeq;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public boolean isRenderCheck() {
		return renderCheck;
	}
	public void setRenderCheck(boolean renderCheck) {
		this.renderCheck = renderCheck;
	}
	public boolean isCheckValor() {
		return checkValor;
	}
	public void setCheckValor(boolean checkValor) {
		this.checkValor = checkValor;
	}
	public Integer getVvqQusQutSeq() {
		return vvqQusQutSeq;
	}
	public void setVvqQusQutSeq(Integer vvqQusQutSeq) {
		this.vvqQusQutSeq = vvqQusQutSeq;
	}
	public Short getVvqQusSeqP() {
		return vvqQusSeqP;
	}
	public void setVvqQusSeqP(Short vvqQusSeqP) {
		this.vvqQusSeqP = vvqQusSeqP;
	}
	public Short getVvqSeqP() {
		return vvqSeqP;
	}
	public void setVvqSeqP(Short vvqSeqP) {
		this.vvqSeqP = vvqSeqP;
	}
	
	public enum Fields {

		PERGUNTA("pergunta"),
		RESPOSTA("resposta"),
		ORDEM_VISUALIZACAO("ordem"),
		QUS_QUT_SEQ("qusQutSeq"),
		QUS_SEQP("qusSeqP"),
		SEQP("seqP"),
		P_EVO_SEQ("pEvoSeq"),
		V_ESP_SEQ("vEspSeq"),
		VVQ_QUS_QUT_SEQ("vvqQusQutSeq"),
		VVQ_QUS_SEQP("vvqQusSeqP"),
		VVQ_SEQP("vvqSeqP");

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