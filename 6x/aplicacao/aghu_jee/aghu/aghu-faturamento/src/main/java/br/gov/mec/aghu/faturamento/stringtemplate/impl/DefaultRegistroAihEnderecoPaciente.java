package br.gov.mec.aghu.faturamento.stringtemplate.impl;

import br.gov.mec.aghu.faturamento.stringtemplate.NullToZeroHelper;
import br.gov.mec.aghu.faturamento.stringtemplate.renderer.AttributeFormatPairRendererHelper;

public class DefaultRegistroAihEnderecoPaciente
		extends
			AbstractRegistroAihEnderecoPaciente {

	private final Short tpLogradouro;
	private final String logrPac;
	private final Integer nuEndPac;
	private final String complEndPac;
	private final String bairroPac;
	private final Integer codMunEndPac;
	private final String ufPac;
	private final Integer cepPac;

	public DefaultRegistroAihEnderecoPaciente(final AttributeFormatPairRendererHelper rendererHelper,
			final Short tpLogradouro,
			final String logrPac,
			final Integer nuEndPac,
			final String complEndPac,
			final String bairroPac,
			final Integer codMunEndPac,
			final String ufPac,
			final Integer cepPac) {

		super(rendererHelper);
		this.tpLogradouro = tpLogradouro;
		this.logrPac = logrPac;
		this.nuEndPac = nuEndPac;
		this.complEndPac = complEndPac;
		this.bairroPac = bairroPac;
		this.codMunEndPac = codMunEndPac;
		this.ufPac = ufPac;
		this.cepPac = cepPac;
	}

	@Override
	public Short getTpLogradouro() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.tpLogradouro, this.isNullToZeroed());
	}

	@Override
	public String getLogrPac() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.logrPac, this.isNullToZeroed());
	}

	@Override
	public Integer getNuEndPac() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.nuEndPac, this.isNullToZeroed());
	}

	@Override
	public String getComplEndPac() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.complEndPac, this.isNullToZeroed());
	}

	@Override
	public String getBairroPac() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.bairroPac, this.isNullToZeroed());
	}

	@Override
	public Integer getCodMunEndPac() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.codMunEndPac, this.isNullToZeroed());
	}

	@Override
	public String getUfPac() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.ufPac, this.isNullToZeroed());
	}

	@Override
	public Integer getCepPac() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.cepPac, this.isNullToZeroed());
	}

	@Override
	public String getDescricao() {

		return "cep: " + this.getCepPac();
	}
}
