package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class TotaisAIHGeralVO implements Serializable {

	private static final long serialVersionUID = -7030576649982338198L;
	
	private Long iphCodSusRealiz;
	private BigDecimal valorSadtRealiz;
	private BigDecimal valorShRealiz;
	private BigDecimal valorSpRealiz;
	private BigDecimal total;

	public Long getIphCodSusRealiz() {
		return iphCodSusRealiz;
	}

	public void setIphCodSusRealiz(Long iphCodSusRealiz) {
		this.iphCodSusRealiz = iphCodSusRealiz;
	}

	public BigDecimal getValorSadtRealiz() {
		return valorSadtRealiz;
	}

	public void setValorSadtRealiz(BigDecimal valorSadtRealiz) {
		this.valorSadtRealiz = valorSadtRealiz;
	}

	public BigDecimal getValorShRealiz() {
		return valorShRealiz;
	}

	public void setValorShRealiz(BigDecimal valorShRealiz) {
		this.valorShRealiz = valorShRealiz;
	}

	public BigDecimal getValorSpRealiz() {
		return valorSpRealiz;
	}

	public void setValorSpRealiz(BigDecimal valorSpRealiz) {
		this.valorSpRealiz = valorSpRealiz;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public enum Fields {
		QUANT_AIH("iphCodSusRealiz"),
		SADT_AIH("valorSadtRealiz"),
		HOSP_AIH("valorShRealiz"),
		PROF_AIH("valorSpRealiz"),
		TOTAL("total")
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

}
