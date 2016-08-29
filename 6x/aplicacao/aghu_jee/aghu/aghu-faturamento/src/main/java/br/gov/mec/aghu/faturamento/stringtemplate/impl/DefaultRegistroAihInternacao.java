package br.gov.mec.aghu.faturamento.stringtemplate.impl;

import br.gov.mec.aghu.faturamento.stringtemplate.NullToZeroHelper;
import br.gov.mec.aghu.faturamento.stringtemplate.renderer.AttributeFormatPairRendererHelper;

public class DefaultRegistroAihInternacao
		extends
			AbstractRegistroAihInternacao {

	private final Long nuProntuario;
	private final String nuEnfermaria;
	private final String nuLeito;

	public DefaultRegistroAihInternacao(final AttributeFormatPairRendererHelper rendererHelper,
			final Long nuProntuario,
			final String nuEnfermaria,
			final String nuLeito) {

		super(rendererHelper);
		this.nuProntuario = nuProntuario;
		this.nuEnfermaria = nuEnfermaria;
		this.nuLeito = nuLeito;
	}

	@Override
	public Long getNuProntuario() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.nuProntuario, this.isNullToZeroed());
	}

	@Override
	public String getNuEnfermaria() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.nuEnfermaria, this.isNullToZeroed());
	}

	@Override
	public String getNuLeito() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.nuLeito, this.isNullToZeroed());
	}

	@Override
	public String getDescricao() {

		return "pront: " + this.getNuProntuario();
	}
}
