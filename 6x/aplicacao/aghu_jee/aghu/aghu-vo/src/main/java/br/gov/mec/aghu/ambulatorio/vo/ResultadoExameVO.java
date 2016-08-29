package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class ResultadoExameVO implements Serializable {
	
	private static final long serialVersionUID = -8271717334928009823L;
	
	private BigDecimal valor;
	private Integer rcdGtcSeq;
	private Integer rcdSeqp;
	private Integer cacSeq;
	private String pclVelEmaExaSigla;
	private Integer pclVelEmaManSeq;
	private Integer pclVelSeqp;
	private Integer pclCalSeq;
	private Integer pclSeqp;
	private Integer seqp;
	private String descricao;
	
	public enum Fields {
		VALOR("valor"),
		DESCRICAO("descricao"),
		RCD_GTC_SEQ("rcdGtcSeq"),
		RCD_SEQP("rcdSeqp"),
		CAC_SEQ("cacSeq"),
		PCL_VEL_EMA_EXA_SIGLA("pclVelEmaExaSigla"),
		PCL_VEL_EMA_MAN_SEQ("pclVelEmaManSeq"),
		PCL_VEL_SEQP("pclVelSeqp"),
		PCL_CAL_SEQ("pclCalSeq"),
		PCL_SEQP("pclSeqp"),
		SEQP("seqp");
		
		private String fields;
		
		private Fields(String fields) {
			this.fields = fields;
		}
		
		@Override
		public String toString() {
			return fields;
		}		
	}
	
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	public Integer getRcdGtcSeq() {
		return rcdGtcSeq;
	}
	public void setRcdGtcSeq(Integer rcdGtcSeq) {
		this.rcdGtcSeq = rcdGtcSeq;
	}
	public Integer getRcdSeqp() {
		return rcdSeqp;
	}
	public void setRcdSeqp(Integer rcdSeqp) {
		this.rcdSeqp = rcdSeqp;
	}
	public Integer getCacSeq() {
		return cacSeq;
	}
	public void setCacSeq(Integer cacSeq) {
		this.cacSeq = cacSeq;
	}
	public String getPclVelEmaExaSigla() {
		return pclVelEmaExaSigla;
	}
	public void setPclVelEmaExaSigla(String pclVelEmaExaSigla) {
		this.pclVelEmaExaSigla = pclVelEmaExaSigla;
	}
	public Integer getPclVelEmaManSeq() {
		return pclVelEmaManSeq;
	}
	public void setPclVelEmaManSeq(Integer pclVelEmaManSeq) {
		this.pclVelEmaManSeq = pclVelEmaManSeq;
	}
	public Integer getPclVelSeqp() {
		return pclVelSeqp;
	}
	public void setPclVelSeqp(Integer pclVelSeqp) {
		this.pclVelSeqp = pclVelSeqp;
	}
	public Integer getPclCalSeq() {
		return pclCalSeq;
	}
	public void setPclCalSeq(Integer pclCalSeq) {
		this.pclCalSeq = pclCalSeq;
	}
	public Integer getPclSeqp() {
		return pclSeqp;
	}
	public void setPclSeqp(Integer pclSeqp) {
		this.pclSeqp = pclSeqp;
	}
	public Integer getSeqp() {
		return seqp;
	}
	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}
	
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
