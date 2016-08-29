package br.gov.mec.aghu.faturamento.stringtemplate.impl;

import br.gov.mec.aghu.faturamento.stringtemplate.NullToZeroHelper;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioCpfCnsCnpjCnes;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioInEquipe;
import br.gov.mec.aghu.faturamento.stringtemplate.renderer.AttributeFormatPairRendererHelper;

public class DefaultRegistroAihProcedimentosSecundariosEspeciais
		extends
			AbstractRegistroAihProcedimentosSecundariosEspeciais {

	private final DominioCpfCnsCnpjCnes inProf;
	private final Long identProf;
	private final Integer cboProf;
	private final DominioInEquipe inEquipe;
	private final DominioCpfCnsCnpjCnes inServico;
	private final Long identServico;
	private final DominioCpfCnsCnpjCnes inExecutor;
	private final Long identExecutor;
	private final Long codProced;
	private final Short qtdProced;
	private final String cmpt;

	public DefaultRegistroAihProcedimentosSecundariosEspeciais(final AttributeFormatPairRendererHelper rendererHelper,
			final DominioCpfCnsCnpjCnes inProf,
			final Long identProf,
			final Integer cboProf,
			final DominioInEquipe inEquipe,
			final DominioCpfCnsCnpjCnes inServico,
			final Long identServico,
			final DominioCpfCnsCnpjCnes inExecutor,
			final Long identExecutor,
			final Long codProced,
			final Short qtdProced,
			final String cmpt) {

		super(rendererHelper);
		this.inProf = inProf;
		this.identProf = identProf;
		this.cboProf = cboProf;
		this.inEquipe = inEquipe;
		this.inServico = inServico;
		this.identServico = identServico;
		this.inExecutor = inExecutor;
		this.identExecutor = identExecutor;
		this.codProced = codProced;
		this.qtdProced = qtdProced;
		this.cmpt = cmpt;
	}

	@Override
	public DominioCpfCnsCnpjCnes getInProf() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.inProf, this.isNullToZeroed());
	}

	@Override
	public Long getIdentProf() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.identProf, this.isNullToZeroed());
	}

	@Override
	public Integer getCboProf() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.cboProf, this.isNullToZeroed());
	}

	@Override
	public DominioInEquipe getInEquipe() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.inEquipe, this.isNullToZeroed());
	}

	@Override
	public DominioCpfCnsCnpjCnes getInServico() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.inServico, this.isNullToZeroed());
	}

	@Override
	public Long getIdentServico() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.identServico, this.isNullToZeroed());
	}

	@Override
	public DominioCpfCnsCnpjCnes getInExecutor() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.inExecutor, this.isNullToZeroed());
	}

	@Override
	public Long getIdentExecutor() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.identExecutor, this.isNullToZeroed());
	}

	@Override
	public Long getCodProced() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.codProced, this.isNullToZeroed());
	}

	@Override
	public Short getQtdProced() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.qtdProced, this.isNullToZeroed());
	}

	@Override
	public String getCmpt() {

		return NullToZeroHelper.getInstance().getNullZeroedValue(
				this.cmpt, this.isNullToZeroed());
	}

	@Override
	public String getDescricao() {

		return "cod proc: " + this.getCodProced();
	}
}
