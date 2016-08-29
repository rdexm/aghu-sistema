package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;

public class NutricaoEnteralDigitadaVO {

	private Integer seq;
	private Date dtencerramento;
	private Long nroaih;
	private String indsituacao;
	

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Date getDtencerramento() {
		return dtencerramento;
	}

	public void setDtencerramento(Date dtencerramento) {
		this.dtencerramento = dtencerramento;
	}

	public String getIndsituacao() {
		return indsituacao;
	}

	public void setIndsituacao(String indsituacao) {
		this.indsituacao = indsituacao;
	}
	
	public Long getNroaih() {
		return nroaih;
	}

	public void setNroaih(Long nroaih) {
		this.nroaih = nroaih;
	}


	public enum Fields {
		SEQ("seq"),
		DT_ENCERRAMENTO("dtencerramento"),
		NRO_AIH("nroaih"),
		IND_SITUACAO("indsituacao");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	
	
}
