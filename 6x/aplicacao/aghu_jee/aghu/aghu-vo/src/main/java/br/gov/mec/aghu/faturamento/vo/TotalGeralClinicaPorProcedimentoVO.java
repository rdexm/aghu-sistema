package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.math.BigDecimal;


public class TotalGeralClinicaPorProcedimentoVO implements Serializable{

	private static final long serialVersionUID = -576392762659994000L;

	private BigDecimal valorUti;
	private BigDecimal valorAcomp;
	private Long diariasAcompanhante;
	private Long diasUti;
	
	private BigDecimal valorShUti;
	private BigDecimal valorSpUti;
	private BigDecimal valorSadtUti;
	private BigDecimal valorShAcomp;
	private BigDecimal valorSpAcomp;
	private BigDecimal valorSadtAcomp;

	public BigDecimal getValorUti() {
		return valorUti;
	}

	public void setValorUti(BigDecimal valorUti) {
		this.valorUti = valorUti;
	}

	public BigDecimal getValorAcomp() {
		return valorAcomp;
	}

	public void setValorAcomp(BigDecimal valorAcomp) {
		this.valorAcomp = valorAcomp;
	}

	public Long getDiariasAcompanhante() {
		return diariasAcompanhante;
	}

	public void setDiariasAcompanhante(Long diariasAcompanhante) {
		this.diariasAcompanhante = diariasAcompanhante;
	}

	public Long getDiasUti() {
		return diasUti;
	}

	public void setDiasUti(Long diasUti) {
		this.diasUti = diasUti;
	}

	public BigDecimal getValorShUti() {
		return valorShUti;
	}

	public void setValorShUti(BigDecimal valorShUti) {
		this.valorShUti = valorShUti;
	}

	public BigDecimal getValorSpUti() {
		return valorSpUti;
	}

	public void setValorSpUti(BigDecimal valorSpUti) {
		this.valorSpUti = valorSpUti;
	}

	public BigDecimal getValorSadtUti() {
		return valorSadtUti;
	}

	public void setValorSadtUti(BigDecimal valorSadtUti) {
		this.valorSadtUti = valorSadtUti;
	}

	public BigDecimal getValorShAcomp() {
		return valorShAcomp;
	}

	public void setValorShAcomp(BigDecimal valorShAcomp) {
		this.valorShAcomp = valorShAcomp;
	}

	public BigDecimal getValorSpAcomp() {
		return valorSpAcomp;
	}

	public void setValorSpAcomp(BigDecimal valorSpAcomp) {
		this.valorSpAcomp = valorSpAcomp;
	}

	public BigDecimal getValorSadtAcomp() {
		return valorSadtAcomp;
	}

	public void setValorSadtAcomp(BigDecimal valorSadtAcomp) {
		this.valorSadtAcomp = valorSadtAcomp;
	}

	public enum Fields {
		 VALOR_UTI("valorUti")
		,VALOR_ACOMP("valorAcomp")
		,DIAS_ACOMP("diariasAcompanhante")
		,DIAS_UTI("diasUti")
		
		,VALOR_SH_UTI("valorShUti")
		,VALOR_SP_UTI("valorSpUti")
		,VALOR_SADT_UTI("valorSadtUti")
		,VALOR_SH_ACOMP("valorShAcomp")
		,VALOR_SP_ACOMP("valorSpAcomp")
		,VALOR_SADT_ACOMP("valorSadtAcomp")
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
