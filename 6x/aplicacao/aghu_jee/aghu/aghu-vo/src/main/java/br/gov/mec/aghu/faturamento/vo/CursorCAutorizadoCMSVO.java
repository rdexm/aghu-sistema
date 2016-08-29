package br.gov.mec.aghu.faturamento.vo;


public class CursorCAutorizadoCMSVO implements java.io.Serializable {

	private static final long serialVersionUID = 4026373737099966547L;

	private Long codSusCMA;
	private Long qtdeProcCma;

	public enum Fields {
		COD_SUS_CMA("codSusCMA"), QTDE_PROC_CMA("qtdeProcCma");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	@Override
	public String toString() {
		return "CursorCAutorizadoCMSVO [codSusCMA=" + codSusCMA
				+ ", qtdeProcCma=" + qtdeProcCma + "]";
	}
	public Long getCodSusCMA() {
		return codSusCMA;
	}

	public void setCodSusCMA(Long codSusCMA) {
		this.codSusCMA = codSusCMA;
	}

	public Long getQtdeProcCma() {
		return qtdeProcCma;
	}

	public void setQtdeProcCma(Long qtdeProcCma) {
		this.qtdeProcCma = qtdeProcCma;
	}

}