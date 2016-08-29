package br.gov.mec.aghu.faturamento.stringtemplate.impl;

import br.gov.mec.aghu.faturamento.stringtemplate.NullToZeroHelper;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioGrauInstru;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioStGestrisco;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioTpContracep;
import br.gov.mec.aghu.faturamento.stringtemplate.renderer.AttributeFormatPairRendererHelper;

public class DefaultRegistroAihParto
		extends
			AbstractRegistroAihParto {

	private final Byte qtVivos;
	private final Byte qtMortos;
	private final Byte qtAlta;
	private final Byte qtTransf;
	private final Byte qtObito;
	private final Byte qtFilhos;
	private final DominioGrauInstru grauInstru;
	private final String cidIndicacao;
	private final DominioTpContracep tpContracep1;
	private final DominioTpContracep tpContracep2;
	private final DominioStGestrisco stGestrisco;
	private final Long nuPrenatal;

	public DefaultRegistroAihParto(final AttributeFormatPairRendererHelper rendererHelper,
			final Byte qtVivos,
			final Byte qtMortos,
			final Byte qtAlta,
			final Byte qtTransf,
			final Byte qtObito,
			final Byte qtFilhos,
			final DominioGrauInstru grauInstru,
			final String cidIndicacao,
			final DominioTpContracep tpContracep1,
			final DominioTpContracep tpContracep2,
			final DominioStGestrisco stGestrisco,
			final Long nuPrenatal) {

		super(rendererHelper);
		this.qtVivos = qtVivos;
		this.qtMortos = qtMortos;
		this.qtAlta = qtAlta;
		this.qtTransf = qtTransf;
		this.qtObito = qtObito;
		this.qtFilhos = qtFilhos;
		this.grauInstru = grauInstru;
		this.cidIndicacao = cidIndicacao;
		this.tpContracep1 = tpContracep1;
		this.tpContracep2 = tpContracep2;
		this.stGestrisco = stGestrisco;
		this.nuPrenatal = nuPrenatal;
	}

	@Override
	public Byte getQtVivos() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.qtVivos, this.isNullToZeroed());
	}

	@Override
	public Byte getQtMortos() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.qtMortos, this.isNullToZeroed());
	}

	@Override
	public Byte getQtAlta() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.qtAlta, this.isNullToZeroed());
	}

	@Override
	public Byte getQtTransf() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.qtTransf, this.isNullToZeroed());
	}

	@Override
	public Byte getQtObito() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.qtObito, this.isNullToZeroed());
	}

	@Override
	public Byte getQtFilhos() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.qtFilhos, this.isNullToZeroed());
	}

	@Override
	public DominioGrauInstru getGrauInstru() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.grauInstru, this.isNullToZeroed());
	}

	@Override
	public String getCidIndicacao() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.cidIndicacao, this.isNullToZeroed());
	}

	@Override
	public DominioTpContracep getTpContracep1() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.tpContracep1, this.isNullToZeroed());
	}

	@Override
	public DominioTpContracep getTpContracep2() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.tpContracep2, this.isNullToZeroed());
	}

	@Override
	public DominioStGestrisco getStGestrisco() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.stGestrisco, this.isNullToZeroed());
	}

	@Override
	public Long getNuPrenatal() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(this.nuPrenatal, this.isNullToZeroed());
	}

	@Override
	public String getDescricao() {

		return "nu prenatal: " + this.getNuPrenatal();
	}
}
