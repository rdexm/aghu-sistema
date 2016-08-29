package br.gov.mec.aghu.faturamento.stringtemplate.impl;

import br.gov.mec.aghu.faturamento.stringtemplate.NullToZeroHelper;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioSaidaUtineo;
import br.gov.mec.aghu.faturamento.stringtemplate.renderer.AttributeFormatPairRendererHelper;

public class DefaultRegistroAihUtiNeonatal
		extends
			AbstractRegistroAihUtiNeonatal {

	private final DominioSaidaUtineo saidaUtineo;
	private final Integer pesoUtineo;
	private final Integer mesgestUtineo;

	public DefaultRegistroAihUtiNeonatal(final AttributeFormatPairRendererHelper rendererHelper,
			final DominioSaidaUtineo saidaUtineo,
			final Integer pesoUtineo,
			final Integer mesgestUtineo) {

		super(rendererHelper);
		this.saidaUtineo = saidaUtineo;
		this.pesoUtineo = pesoUtineo;
		this.mesgestUtineo = mesgestUtineo;
	}

	public DefaultRegistroAihUtiNeonatal(final AttributeFormatPairRendererHelper rendererHelper) {

		this(rendererHelper,
				null,
				null,
				null);
	}

	@Override
	public DominioSaidaUtineo getSaidaUtineo() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.saidaUtineo, this.isNullToZeroed());
	}

	@Override
	public Integer getPesoUtineo() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.pesoUtineo, this.isNullToZeroed());
	}

	@Override
	public Integer getMesgestUtineo() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.mesgestUtineo, this.isNullToZeroed());
	}

	@Override
	public String getDescricao() {

		return "mesgest: " + this.getMesgestUtineo() + " peso: " + this.getPesoUtineo();
	}
}
