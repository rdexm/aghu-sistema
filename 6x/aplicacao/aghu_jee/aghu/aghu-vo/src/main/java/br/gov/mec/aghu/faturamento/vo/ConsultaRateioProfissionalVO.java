package br.gov.mec.aghu.faturamento.vo;

import java.math.BigDecimal;

public class ConsultaRateioProfissionalVO {

	private Long procedimentoHosp;

	private String descricao;

	private Long quantidade;

	private BigDecimal vlrProcedimento;
	
	private BigDecimal vlrServProfReport;

	private Short codProcedimento;

	private Long countCodProcedimento;
	
	
	
	
	public Short getCodProcedimento() {
		return codProcedimento;
	}

	public void setCodProcedimento(Short codProcedimento) {
		this.codProcedimento = codProcedimento;
	}

	public Long getProcedimentoHosp() {
		return procedimentoHosp;
	}

	public void setProcedimentoHosp(Long procedimentoHosp) {
		this.procedimentoHosp = procedimentoHosp;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Long getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Long quantidade) {
		this.quantidade = quantidade;
	}

	public BigDecimal getVlrProcedimento() {
		return vlrProcedimento;
	}

	public void setVlrProcedimento(BigDecimal vlrProcedimento) {
		this.vlrProcedimento = vlrProcedimento;
	}
	
	public BigDecimal getVlrServProfReport() {
		return vlrServProfReport;
	}

	public void setVlrServProfReport(BigDecimal vlrServProfReport) {
		this.vlrServProfReport = vlrServProfReport;
	}
	
	public Long getCountCodProcedimento() {
		return countCodProcedimento;
	}

	public void setCountCodProcedimento(Long countCodProcedimento) {
		this.countCodProcedimento = countCodProcedimento;
	}

	public enum Fields {

		DESCRICAO("descricao"), //
		PROCEDIMENTO_HOSP("procedimentoHosp"), //
		QUANTIDADE("quantidade"), //
		VALOR_PROCEDIMENTO("vlrProcedimento"), //
		CODIGO_PROCEDIMENTO("codProcedimento"), //
		COUNT_COD_PROCED("countCodProcedimento"), //
		VLR_SERV_PROF_REPORT("vlrServProfReport");//

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

}