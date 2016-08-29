package br.gov.mec.aghu.faturamento.stringtemplate.impl;

import java.math.BigInteger;
import java.util.Date;

import br.gov.mec.aghu.faturamento.stringtemplate.NullToZeroHelper;
import br.gov.mec.aghu.faturamento.stringtemplate.renderer.AttributeFormatPairRendererHelper;

public class DefaultRegistroAihComum
		extends
			AbstractRegistroAihComum {

	private final Long nuLote;
	private final Integer qtLote;
	private final Date apresLote;
	private final Integer seqLote;
	private final String orgEmisAih;
	private final BigInteger cnesHosp;
	private final Integer munHosp;
	private final Long nuAih;
	private final Short identAih;
	private final Byte especAih;

	public DefaultRegistroAihComum(final AttributeFormatPairRendererHelper rendererHelper,
			final Long nuLote,
			final Integer qtLote,
			final Date apresLote,
			final Integer seqLote,
			final String orgEmisAih,
			final BigInteger cnesHosp,
			final Integer munHosp,
			final Long nuAih,
			final Short identAih,
			final Byte especAih) {

		super(rendererHelper);
		this.nuLote = nuLote;
		this.qtLote = qtLote;
		this.apresLote = apresLote;
		this.seqLote = seqLote;
		this.orgEmisAih = orgEmisAih;
		this.cnesHosp = cnesHosp;
		this.munHosp = munHosp;
		this.nuAih = nuAih;
		this.identAih = identAih;
		this.especAih = especAih;
	}

	@Override
	public Long getNuLote() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.nuLote, this.isNullToZeroed());
	}

	@Override
	public Integer getQtLote() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.qtLote, this.isNullToZeroed());
	}

	@Override
	public Date getApresLote() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.apresLote, this.isNullToZeroed());
	}

	@Override
	public Integer getSeqLote() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.seqLote, this.isNullToZeroed());
	}

	@Override
	public String getOrgEmisAih() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.orgEmisAih, this.isNullToZeroed());
	}

	@Override
	public BigInteger getCnesHosp() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.cnesHosp, this.isNullToZeroed());
	}

	@Override
	public Integer getMunHosp() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.munHosp, this.isNullToZeroed());
	}

	@Override
	public Long getNuAih() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.nuAih, this.isNullToZeroed());
	}

	@Override
	public Short getIdentAih() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.identAih, this.isNullToZeroed());
	}

	@Override
	public Byte getEspecAih() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.especAih, this.isNullToZeroed());
	}

	@Override
	public String getDescricao() {

		return "lote: " + this.getNuLote() + " seq: " + this.getSeqLote();
	}
}
