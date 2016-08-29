package br.gov.mec.aghu.faturamento.stringtemplate.impl;

import java.math.BigInteger;

import br.gov.mec.aghu.faturamento.stringtemplate.NullToZeroHelper;
import br.gov.mec.aghu.faturamento.stringtemplate.renderer.AttributeFormatPairRendererHelper;

public class DefaultRegistroAihDadosOpm
		extends
			AbstractRegistroAihDadosOpm {

	private final Long codOpm;
	private final Short linha;
	private final String regAnvisa;
	private final String serie;
	private final String lote;
	private final BigInteger notaFiscal;
	private final Long cnpjForn;
	private final Long cnpjFabric;

	public DefaultRegistroAihDadosOpm(
			final AttributeFormatPairRendererHelper rendererHelper,
			final Long codOpm,
			final Short linha,
			final String regAnvisa,
			final String serie,
			final String lote,
			final BigInteger notaFiscal,
			final Long cnpjForn,
			final Long cnpjFabric) {

		super(rendererHelper);
		this.codOpm = codOpm;
		this.linha = linha;
		this.regAnvisa = regAnvisa;
		this.serie = serie;
		this.lote = lote;
		this.notaFiscal = notaFiscal;
		this.cnpjForn = cnpjForn;
		this.cnpjFabric = cnpjFabric;
	}

	@Override
	public Long getCodOpm() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.codOpm, this.isNullToZeroed());
	}

	@Override
	public Short getLinha() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.linha, this.isNullToZeroed());
	}

	@Override
	public String getRegAnvisa() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.regAnvisa, this.isNullToZeroed());
	}

	@Override
	public String getSerie() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.serie, this.isNullToZeroed());
	}

	@Override
	public String getLote() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.lote, this.isNullToZeroed());
	}

	@Override
	public BigInteger getNotaFiscal() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.notaFiscal, this.isNullToZeroed());
	}

	@Override
	public Long getCnpjForn() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.cnpjForn, this.isNullToZeroed());
	}

	@Override
	public Long getCnpjFabric() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.cnpjFabric, this.isNullToZeroed());
	}

	@Override
	public String getDescricao() {

		return "opm: " + this.getCodOpm();
	}
}
