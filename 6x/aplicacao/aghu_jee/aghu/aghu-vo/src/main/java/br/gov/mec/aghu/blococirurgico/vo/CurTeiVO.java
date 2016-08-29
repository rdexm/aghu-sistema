package br.gov.mec.aghu.blococirurgico.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class CurTeiVO implements BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3935323377393940598L;

	private Integer atdSeq;
	private Short seqP;
	private Integer conNumero;
	private Integer espSeq;
	private Short unfSeq;

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Short getSeqP() {
		return seqP;
	}

	public void setSeqP(Short seqP) {
		this.seqP = seqP;
	}

	public Integer getConNumero() {
		return conNumero;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}

	public enum Fields {
		ATD_SEQ("atdSeq"), 
		SEQ_P("seqP"), 
		ESP_SEQ("espSeq"), 
		UNF_SEQ("unfSeq"), 
		CON_NUMERO("conNumero");
		
		private String fields;
		
		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Integer getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Integer espSeq) {
		this.espSeq = espSeq;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}
}
