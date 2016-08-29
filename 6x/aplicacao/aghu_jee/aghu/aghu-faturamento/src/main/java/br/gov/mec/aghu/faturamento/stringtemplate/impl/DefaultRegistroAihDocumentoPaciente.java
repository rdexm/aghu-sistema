package br.gov.mec.aghu.faturamento.stringtemplate.impl;

import java.math.BigInteger;

import br.gov.mec.aghu.faturamento.stringtemplate.NullToZeroHelper;
import br.gov.mec.aghu.faturamento.stringtemplate.renderer.AttributeFormatPairRendererHelper;

public class DefaultRegistroAihDocumentoPaciente
		extends
			AbstractRegistroAihDocumentoPaciente {

	private final BigInteger nuDocpac;

	public DefaultRegistroAihDocumentoPaciente(final AttributeFormatPairRendererHelper rendererHelper,
			final BigInteger nuDocpac) {

		super(rendererHelper);
		this.nuDocpac = nuDocpac;
	}

	@Override
	public BigInteger getNuDocPac() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.nuDocpac, this.isNullToZeroed());
	}

	@Override
	public String getDescricao() {

		return "doc: " + this.getNuDocPac();
	}
}
