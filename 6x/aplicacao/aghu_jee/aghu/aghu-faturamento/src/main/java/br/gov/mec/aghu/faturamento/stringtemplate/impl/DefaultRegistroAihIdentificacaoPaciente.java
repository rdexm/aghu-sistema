package br.gov.mec.aghu.faturamento.stringtemplate.impl;

import java.math.BigInteger;
import java.util.Date;

import br.gov.mec.aghu.faturamento.stringtemplate.NullToZeroHelper;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioTipoDocPac;
import br.gov.mec.aghu.faturamento.stringtemplate.renderer.AttributeFormatPairRendererHelper;

public class DefaultRegistroAihIdentificacaoPaciente
		extends
			AbstractRegistroAihIdentificacaoPaciente {

	private final String nmPaciente;
	private final Date dtNascPac;
	private final String sexoPac;
	private final String racaCor;
	private final String nmMaePac;
	private final String nmRespPac;
	private final DominioTipoDocPac tpDocPac;
	private final Integer etniaIndigena;
	private final BigInteger nuCns;
	private final Short nacPac;

	public DefaultRegistroAihIdentificacaoPaciente(final AttributeFormatPairRendererHelper rendererHelper,
			final String nmPaciente,
			final Date dtNascPac,
			final String sexoPac,
			final String racaCor,
			final String nmMaePac,
			final String nmRespPac,
			final DominioTipoDocPac tpDocPac,
			final Integer etniaIndigena,
			final BigInteger nuCns,
			final Short nacPac) {

		super(rendererHelper);
		this.nmPaciente = nmPaciente;
		this.dtNascPac = dtNascPac;
		this.sexoPac = sexoPac;
		this.racaCor = racaCor;
		this.nmMaePac = nmMaePac;
		this.nmRespPac = nmRespPac;
		this.tpDocPac = tpDocPac;
		this.etniaIndigena = etniaIndigena;
		this.nuCns = nuCns;
		this.nacPac = nacPac;
	}

	@Override
	public String getNmPaciente() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.nmPaciente, this.isNullToZeroed());
	}

	@Override
	public Date getDtNascPac() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.dtNascPac, this.isNullToZeroed());
	}

	@Override
	public String getSexoPac() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.sexoPac, this.isNullToZeroed());
	}

	@Override
	public String getRacaCor() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.racaCor, this.isNullToZeroed());
	}

	@Override
	public String getNmMaePac() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.nmMaePac, this.isNullToZeroed());
	}

	@Override
	public String getNmRespPac() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.nmRespPac, this.isNullToZeroed());
	}

	@Override
	public DominioTipoDocPac getTpDocPac() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.tpDocPac, this.isNullToZeroed());
	}

	@Override
	public Integer getEtniaIndigena() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.etniaIndigena, this.isNullToZeroed());
	}

	@Override
	public BigInteger getNuCns() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.nuCns, this.isNullToZeroed());
	}

	@Override
	public Short getNacPac() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.nacPac, this.isNullToZeroed());
	}

	@Override
	public String getDescricao() {

		return "pac: " + this.getNmPaciente() + " cns: " + this.getNuCns();
	}
}
