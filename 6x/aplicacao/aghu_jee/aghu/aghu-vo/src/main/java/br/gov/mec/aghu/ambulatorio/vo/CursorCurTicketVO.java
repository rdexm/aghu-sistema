package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

public class CursorCurTicketVO implements Serializable {

	private static final long serialVersionUID = -1564937478733188845L;
	
	private String texto = "4";
	private Long seq;
	private String indPendente;
	private String indImpresso;
	
	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	public String getIndPendente() {
		return indPendente;
	}

	public void setIndPendente(String indPendente) {
		this.indPendente = indPendente;
	}

	public String getIndImpresso() {
		return indImpresso;
	}

	public void setIndImpresso(String indImpresso) {
		this.indImpresso = indImpresso;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public enum Fields {
		SEQ("seq"),
		IND_PENDENTE("indPendente"),
		IND_IMPRESSO("indImpresso")
		;

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
}