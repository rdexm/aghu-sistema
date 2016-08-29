package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;


public class CursorBuscaOutraVO implements Serializable {

	private static final long serialVersionUID = -3330563945964105925L;

	private Long numero;
	private Integer phiSeq;

	public enum Fields {
		NUMERO("numero"),
		PHI_SEQ("phiSeq")
		;
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Long getNumero() {
		return numero;
	}

	public void setNumero(Long numero) {
		this.numero = numero;
	}

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}
}