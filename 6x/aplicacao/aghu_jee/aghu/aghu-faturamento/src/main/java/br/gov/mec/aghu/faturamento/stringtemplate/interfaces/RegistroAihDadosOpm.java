package br.gov.mec.aghu.faturamento.stringtemplate.interfaces;

import java.math.BigInteger;

public interface RegistroAihDadosOpm
		extends
			RegistroAih {

	public enum NomeAtributoTemplate implements
			NomeAtributoEnum {

		COD_OPM("cod_opm"),
		LINHA("linha"),
		REG_ANVISA("reg_anvisa"),
		SERIE("serie"),
		LOTE("lote"),
		NOTA_FISCAL("nota_fiscal"),
		CNPJ_FORN("cnpj_forn"),
		CNPJ_FABRIC("cnpj_fabric"), ;

		private final String nome;

		private NomeAtributoTemplate(final String nome) {

			this.nome = nome;
		}

		@Override
		public String getNome() {

			return this.nome;
		}
	}

	public abstract Long getCodOpm();

	public abstract Short getLinha();

	public abstract String getRegAnvisa();

	public abstract String getSerie();

	public abstract String getLote();

	public abstract BigInteger getNotaFiscal();

	public abstract Long getCnpjForn();

	public abstract Long getCnpjFabric();
}
