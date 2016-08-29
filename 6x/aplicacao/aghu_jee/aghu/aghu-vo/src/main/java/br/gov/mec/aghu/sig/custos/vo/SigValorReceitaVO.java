package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;


public class SigValorReceitaVO implements java.io.Serializable {
	
	private static final long serialVersionUID = -7160027770511048713L;
	
	private Integer cthSeq;
	private Integer eaiSeqp;
	private Long qtde;
	private Long iphCodSusRealiz;
	private BigDecimal valorProcedRealiz;
	private BigDecimal valorShRealiz;
    private BigDecimal valorSpRealiz;
	private Integer intSeq;
	private Long iphCodSus;
	private BigDecimal valorTotal;
	
	public SigValorReceitaVO() {
		super();
	}

	public Integer getCthSeq() {
		return cthSeq;
	}


	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}


	public Integer getEaiSeqp() {
		return eaiSeqp;
	}


	public void setEaiSeqp(Integer eaiSeqp) {
		this.eaiSeqp = eaiSeqp;
	}


	public Long getQtde() {
		return qtde;
	}


	public void setQtde(Long qtde) {
		this.qtde = qtde;
	}


	public Long getIphCodSusRealiz() {
		return iphCodSusRealiz;
	}


	public void setIphCodSusRealiz(Long iphCodSusRealiz) {
		this.iphCodSusRealiz = iphCodSusRealiz;
	}


	public BigDecimal getValorProcedRealiz() {
		if(valorProcedRealiz == null){
			return BigDecimal.ZERO;
		}
		return valorProcedRealiz;
	}


	public void setValorProcedRealiz(BigDecimal valorProcedRealiz) {
		this.valorProcedRealiz = valorProcedRealiz;
	}


	public BigDecimal getValorShRealiz() {
		if(valorShRealiz == null){
			return BigDecimal.ZERO;
		}
		return valorShRealiz;
	}


	public void setValorShRealiz(BigDecimal valorShRealiz) {
		this.valorShRealiz = valorShRealiz;
	}


	public BigDecimal getValorSpRealiz() {
		if(valorSpRealiz == null){
			return BigDecimal.ZERO;
		}
		return valorSpRealiz;
	}


	public void setValorSpRealiz(BigDecimal valorSpRealiz) {
		this.valorSpRealiz = valorSpRealiz;
	}


	public Integer getIntSeq() {
		return intSeq;
	}


	public void setIntSeq(Integer intSeq) {
		this.intSeq = intSeq;
	}
	
	public enum Fields {
		CTH_SEQ("cthSeq"),
		EAI_SEQP("eaiSeqp"),
		QTDE("qtde"),
		IPH_COD_SUS_REALIZ("iphCodSusRealiz"),
		VALOR_PROCED_REALIZ("valorProcedRealiz"),
		VALOR_SH_REALIZ("valorShRealiz"),
        VALOR_SP_REALIZ("valorSpRealiz"),
		INT_SEQ("intSeq"),
		IPH_COD_SUS("iphCodSus");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public Long getIphCodSus() {
		return iphCodSus;
	}

	public void setIphCodSus(Long iphCodSus) {
		this.iphCodSus = iphCodSus;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

}
