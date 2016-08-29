package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class TotaisProcedEspHemotGeralVO implements Serializable{

	private static final long serialVersionUID = -7161181416892127730L;
	
	private Long quant;
	private BigDecimal valorSadt;
	private BigDecimal valorServHosp;
	private BigDecimal valorServProf;
	private BigDecimal total;

	public Long getQuant() {
		return quant;
	}

	public void setQuant(Long quant) {
		this.quant = quant;
	}

	public BigDecimal getValorSadt() {
		return valorSadt;
	}

	public void setValorSadt(BigDecimal valorSadt) {
		this.valorSadt = valorSadt;
	}

	public BigDecimal getValorServHosp() {
		return valorServHosp;
	}

	public void setValorServHosp(BigDecimal valorServHosp) {
		this.valorServHosp = valorServHosp;
	}

	public BigDecimal getValorServProf() {
		return valorServProf;
	}

	public void setValorServProf(BigDecimal valorServProf) {
		this.valorServProf = valorServProf;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public enum Fields {
		 QUANT("quant")
		,V_SADT("valorSadt")
		,V_HOSP("valorServHosp")
		,V_PROF("valorServProf")
		,TOTAL("total")
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
