package br.gov.mec.aghu.faturamento.stringtemplate;

import br.gov.mec.aghu.faturamento.business.AbstractFatDebugExtraFileLogEnable;
import br.gov.mec.aghu.faturamento.business.GeracaoArquivoLog;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.AjusteFormatoAtributoRegistroAih;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.GeradorRegistroSus;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAih;

public abstract class AbstractGeradorRegistroSus
		extends
			AbstractFatDebugExtraFileLogEnable
		implements
			GeradorRegistroSus {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9163992746343567174L;

	private final AjusteFormatoAtributoRegistroAih formatter;

	protected void ajustarFormatoAtributos(
			final RegistroAih registro) {

		this.formatter.ajustarFormatoAtributos(registro);
	}

	protected AbstractGeradorRegistroSus(
			final GeracaoArquivoLog logger, 
			final AjusteFormatoAtributoRegistroAih formatter) {

		super(logger);

		if (formatter == null) {
			throw new IllegalArgumentException("Parametro formatter nao informado!!!");
		}
		this.formatter = formatter;
	}
}
