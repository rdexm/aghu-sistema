package br.gov.mec.aghu.faturamento.vo;



public class CursorCAtosProtVO {

	private Long codigoOpm;
	private Short linhaProcedimento;
	private String regAnvisaOpm;
	private String serieOpm;
	private String loteOpm;
	private Integer notaFiscal;
	private Long cnpjFornecedor;
	private Long cnpjFabricante;
	
	public enum Fields { 

		CODIGO_OPM("codigoOpm"),
		LINHA_PROCEDIMENTO("linhaProcedimento"),
		REG_ANVISA_OPM("regAnvisaOpm"),
		SERIE_OPM("serieOpm"), 
		LOTE_OPM("loteOpm"),
		NOTA_FISCAL("notaFiscal"),
		CNPJ_FORNECEDOR("cnpjFornecedor"),
		CNPJ_FABRICANTE("cnpjFabricante");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	public Long getCodigoOpm() {
		return codigoOpm;
	}

	public void setCodigoOpm(Long codigoOpm) {
		this.codigoOpm = codigoOpm;
	}

	public Short getLinhaProcedimento() {
		return linhaProcedimento;
	}

	public void setLinhaProcedimento(Short linhaProcedimento) {
		this.linhaProcedimento = linhaProcedimento;
	}

	public String getRegAnvisaOpm() {
		return regAnvisaOpm;
	}

	public void setRegAnvisaOpm(String regAnvisaOpm) {
		this.regAnvisaOpm = regAnvisaOpm;
	}

	public String getSerieOpm() {
		return serieOpm;
	}

	public void setSerieOpm(String serieOpm) {
		this.serieOpm = serieOpm;
	}

	public String getLoteOpm() {
		return loteOpm;
	}

	public void setLoteOpm(String loteOpm) {
		this.loteOpm = loteOpm;
	}

	public Integer getNotaFiscal() {
		return notaFiscal;
	}

	public void setNotaFiscal(Integer notaFiscal) {
		this.notaFiscal = notaFiscal;
	}

	public Long getCnpjFornecedor() {
		return cnpjFornecedor;
	}

	public void setCnpjFornecedor(Long cnpjFornecedor) {
		this.cnpjFornecedor = cnpjFornecedor;
	}

	public Long getCnpjFabricante() {
		return cnpjFabricante;
	}

	public void setCnpjFabricante(Long cnpjFabricante) {
		this.cnpjFabricante = cnpjFabricante;
	}
}