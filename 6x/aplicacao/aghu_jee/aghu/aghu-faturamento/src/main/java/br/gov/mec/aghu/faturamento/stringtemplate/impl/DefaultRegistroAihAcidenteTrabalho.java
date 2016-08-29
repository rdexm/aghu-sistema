package br.gov.mec.aghu.faturamento.stringtemplate.impl;

import br.gov.mec.aghu.faturamento.stringtemplate.NullToZeroHelper;
import br.gov.mec.aghu.faturamento.stringtemplate.renderer.AttributeFormatPairRendererHelper;

public class DefaultRegistroAihAcidenteTrabalho
		extends
			AbstractRegistroAihAcidenteTrabalho {

	private final Long cnpjEmpreg;
	private final Integer cbor;
	private final Integer cnaer;
	private final Integer tpVincprev;

	public DefaultRegistroAihAcidenteTrabalho(final AttributeFormatPairRendererHelper rendererHelper,
			final Long cnpjEmpreg,
			final Integer cbor,
			final Integer cnaer,
			final Integer tpVincprev) {

		super(rendererHelper);

		this.cnpjEmpreg = cnpjEmpreg;
		this.cbor = cbor;
		this.cnaer = cnaer;
		this.tpVincprev = tpVincprev;
	}

	public DefaultRegistroAihAcidenteTrabalho(final AttributeFormatPairRendererHelper rendererHelper) {

		this(rendererHelper,
				null,
				null,
				null,
				null);
	}

	@Override
	public Long getCnpjEmpreg() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.cnpjEmpreg, this.isNullToZeroed());
	}

	@Override
	public Integer getCbor() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.cbor, this.isNullToZeroed());
	}

	@Override
	public Integer getCnaer() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.cnaer, this.isNullToZeroed());
	}

	@Override
	public Integer getTpVincprev() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.tpVincprev, this.isNullToZeroed());
	}

	@Override
	public String getDescricao() {

		return "cnpj: " + this.getCnpjEmpreg();
	}

}
