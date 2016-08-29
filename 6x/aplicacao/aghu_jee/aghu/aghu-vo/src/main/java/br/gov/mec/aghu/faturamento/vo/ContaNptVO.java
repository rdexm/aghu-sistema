package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoConta;



public class ContaNptVO {

	private Integer seq;
	private Date dtEncerramento;
	private Long nroAih;
	private DominioSituacaoConta situacao;
	
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public Date getDtEncerramento() {
		return dtEncerramento;
	}
	public void setDtEncerramento(Date dtEncerramento) {
		this.dtEncerramento = dtEncerramento;
	}
	public Long getNroAih() {
		return nroAih;
	}
	public void setNroAih(Long nroAih) {
		this.nroAih = nroAih;
	}
	public DominioSituacaoConta getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSituacaoConta situacao) {
		this.situacao = situacao;
	}
	
	public enum Fields {
		SEQ("seq"),
		DT_ENCERRAMENTO("dtEncerramento"),
		PRONTUARIO("prontuario"),
		NRO_AIH("nroAih"),
		SITUACAO("situacao");
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
