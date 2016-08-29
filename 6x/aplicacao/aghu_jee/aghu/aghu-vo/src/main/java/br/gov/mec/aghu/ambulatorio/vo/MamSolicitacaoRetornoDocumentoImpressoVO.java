package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

public class MamSolicitacaoRetornoDocumentoImpressoVO implements Serializable {
	
	private static final long serialVersionUID = -530852941770244128L;
	private Long seq;
	private String indPendente;
	
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

	public enum Fields {
		SEQ("seq"),
		IND_PENDENTE("indPendente")
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