package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;


public class CursorBuscaHistoricoVO implements Serializable {

	private static final long serialVersionUID = -1146332329980698669L;
	
	private Integer phiSeq;

	public enum Fields {
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

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}
}