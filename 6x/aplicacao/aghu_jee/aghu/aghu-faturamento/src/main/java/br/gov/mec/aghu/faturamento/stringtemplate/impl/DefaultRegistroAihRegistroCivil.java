package br.gov.mec.aghu.faturamento.stringtemplate.impl;

import java.util.Date;

import br.gov.mec.aghu.faturamento.stringtemplate.NullToZeroHelper;
import br.gov.mec.aghu.faturamento.stringtemplate.renderer.AttributeFormatPairRendererHelper;

public class DefaultRegistroAihRegistroCivil
		extends
			AbstractRegistroAihRegistroCivil {

	private final Long numeroDn;
	private final String nomeRn;
	private final String rsCart;
	private final String livroRn;
	private final Short folhaRn;
	private final Integer termoRn;
	private final Date dtEmisRn;
	private final Short linha;
	private final String matricula;

	public DefaultRegistroAihRegistroCivil(final AttributeFormatPairRendererHelper rendererHelper,
			final Long numeroDn,
			final String nomeRn,
			final String rsCart,
			final String livroRn,
			final Short folhaRn,
			final Integer termoRn,
			final Date dtEmisRn,
			final Short linha,
			final String matricula) {

		super(rendererHelper);
		this.numeroDn = numeroDn;
		this.nomeRn = nomeRn;
		this.rsCart = rsCart;
		this.livroRn = livroRn;
		this.folhaRn = folhaRn;
		this.termoRn = termoRn;
		this.dtEmisRn = dtEmisRn;
		this.linha = linha;
		this.matricula = matricula;
	}

	@Override
	public Long getNumeroDn() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.numeroDn, this.isNullToZeroed());
	}

	@Override
	public String getNomeRn() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.nomeRn, this.isNullToZeroed());
	}

	@Override
	public String getRsCart() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.rsCart, this.isNullToZeroed());
	}

	@Override
	public String getLivroRn() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.livroRn, this.isNullToZeroed());
	}

	@Override
	public Short getFolhaRn() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.folhaRn, this.isNullToZeroed());
	}

	@Override
	public Integer getTermoRn() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.termoRn, this.isNullToZeroed());
	}

	@Override
	public Date getDtEmisRn() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.dtEmisRn, this.isNullToZeroed());
	}

	@Override
	public Short getLinha() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.linha, this.isNullToZeroed());
	}

	@Override
	public String getMatricula() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.matricula, this.isNullToZeroed());
	}

	@Override
	public String getDescricao() {

		return "mat: " + this.getMatricula();
	}
}
