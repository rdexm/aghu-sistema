package br.gov.mec.aghu.compras.contaspagar.impl;

import java.math.BigDecimal;

/**
 * <p>
 * Estrutuar como especificado em <a href=
 * "http://redmine.mec.gov.br/projects/aghu/repository/changes/documentos/Analise/Sprint36/LaranjaMecanica/2179/2179-GerarArquivoContasPeriodo.doc"
 * >http://redmine.mec.gov.br/projects/aghu/repository/changes/documentos/
 * Analise/Sprint36/LaranjaMecanica/2179/2179-GerarArquivoContasPeriodo.doc</a><br/>
 * Acessado em: <code>06.06.2011</code>
 * </p>
 * 
 * @author alejandro
 *
 */

public class RegistroCsvDividaHospitalNaturezaDespesa extends AbstractRegistroCsv {

	protected enum RegistroCsvDividaHospitalNaturezaEnum implements CamposEnum {

		ANO("ano", "Ano"),
		AFN_NTD_GRP_COD("afnNtdGndCodigo","Grupo Natureza HU"),
		AFN_NTD_COD("afnNtdCodigo", "Natureza HU"),
		NTP_GNP_COD("ntdCodigo", "Grupo Natureza SIAFI"),
		NTP_COD("ntpCodigo","Natureza SIAFI"),
		SUM_VALOR("valor", "Valor");

		private final String desc;
		private final String campo;

		private RegistroCsvDividaHospitalNaturezaEnum(final String campo, final String desc) {
			this.campo = campo;
			this.desc = desc;
		}

		@Override
		public int getIndice() {
			return this.ordinal();
		}

		@Override
		public String getDescricao() {
			return this.desc;
		}

		@Override
		public String getCampo() {
			return this.campo;
		}
	}

	private String ano;
	private Integer afnNtdGndCodigo;
	private Byte afnNtdCodigo;
	private BigDecimal ntdCodigo;
	private Byte ntpCodigo;
	private Double valor;
	

	public RegistroCsvDividaHospitalNaturezaDespesa() {
		super(NOME_TEMPLATE_LINHA_PONTO_VIRGULA);
	}

	@Override
	public CamposEnum[] obterCampos() {
		return RegistroCsvDividaHospitalNaturezaEnum.values();
	}

	@Override
	protected Object obterRegistro(final CamposEnum campo) {

		Object result = null;
		result = super.obterRegistro(campo);
		// Colocar tratamentos das colunas aqui
		if(campo == RegistroCsvDividaHospitalNaturezaEnum.SUM_VALOR){
			if(result != null){
				result = String.format("%.2f", result);
			}
		}

		return result;
	}

	public Integer getAfnNtdGndCodigo() {
		return afnNtdGndCodigo;
	}

	public void setAfnNtdGndCodigo(Integer afnNtdGndCodigo) {
		this.afnNtdGndCodigo = afnNtdGndCodigo;
	}

	public Byte getAfnNtdCodigo() {
		return afnNtdCodigo;
	}

	public void setAfnNtdCodigo(Byte afnNtdCodigo) {
		this.afnNtdCodigo = afnNtdCodigo;
	}

	public String getAno() {
		return ano;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}

	public BigDecimal getNtdCodigo() {
		return ntdCodigo;
	}

	public void setNtdCodigo(BigDecimal ntdCodigo) {
		this.ntdCodigo = ntdCodigo;
	}

	public Byte getNtpCodigo() {
		return ntpCodigo;
	}

	public void setNtpCodigo(Byte ntpCodigo) {
		this.ntpCodigo = ntpCodigo;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}
}