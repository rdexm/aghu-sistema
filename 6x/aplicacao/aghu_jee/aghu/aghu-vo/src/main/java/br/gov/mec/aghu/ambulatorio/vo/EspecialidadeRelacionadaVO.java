package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

public class EspecialidadeRelacionadaVO implements Serializable {

	private static final long serialVersionUID = 3357844151818583883L;
	
	private Short seq;
	private Short espSeq;
	private Short uslUnfSeq;
	private String sigla;	
	
	public enum Fields {
		SEQ("seq"),
		ESP_SEQ("espSeq"),
		USL_UNF_SEQ("uslUnfSeq"),
		SIGLA("sigla"),
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

	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	public Short getUslUnfSeq() {
		return uslUnfSeq;
	}

	public void setUslUnfSeq(Short uslUnfSeq) {
		this.uslUnfSeq = uslUnfSeq;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}
}