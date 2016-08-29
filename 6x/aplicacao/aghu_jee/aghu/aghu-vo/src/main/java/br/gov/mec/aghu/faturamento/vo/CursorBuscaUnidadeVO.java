package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;


public class CursorBuscaUnidadeVO implements Serializable {
		
	private static final long serialVersionUID = 2808568431939346261L;
	
	private Short ufeUnfSeq;
	private String indOrigem;
	private Integer ipsRmpSeq;
	private Short unfseq;

	public enum Fields {
		UFE_UNF_SEQ("ufeUnfSeq"),
		IND_ORIGEM("indOrigem"),
		IPS_RMP_SEQ("ipsRmpSeq"),
		UNF_SEQ("unfseq");
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
		
	}

	public Short getUfeUnfSeq() {
		return ufeUnfSeq;
	}

	public void setUfeUnfSeq(Short ufeUnfSeq) {
		this.ufeUnfSeq = ufeUnfSeq;
	}

	public String getIndOrigem() {
		return indOrigem;
	}

	public void setIndOrigem(String indOrigem) {
		this.indOrigem = indOrigem;
	}

	public Integer getIpsRmpSeq() {
		return ipsRmpSeq;
	}

	public void setIpsRmpSeq(Integer ipsRmpSeq) {
		this.ipsRmpSeq = ipsRmpSeq;
	}

	public Short getUnfseq() {
		return unfseq;
	}

	public void setUnfseq(Short unfseq) {
		this.unfseq = unfseq;
	}
}