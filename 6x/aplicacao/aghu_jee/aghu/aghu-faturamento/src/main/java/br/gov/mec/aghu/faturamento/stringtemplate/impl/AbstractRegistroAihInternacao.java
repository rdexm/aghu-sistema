package br.gov.mec.aghu.faturamento.stringtemplate.impl;

import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihInternacao;
import br.gov.mec.aghu.faturamento.stringtemplate.renderer.AttributeFormatPairRendererHelper;

public abstract class AbstractRegistroAihInternacao
		extends
			AbstractRegistroAih
		implements
			RegistroAihInternacao {

	@Override
	protected Object getAtributo(final NomeAtributoEnum atributo) {

		Object result = null;

		if (NomeAtributoTemplate.NU_PRONTUARIO.equals(atributo)) {
			result = this.getNuProntuario();
		} else if (NomeAtributoTemplate.NU_ENFERMARIA.equals(atributo)) {
			result = this.getNuEnfermaria();
		} else if (NomeAtributoTemplate.NU_LEITO.equals(atributo)) {
			result = this.getNuLeito();
		} else {
			throw new IllegalArgumentException("Atributo: " + atributo
					+ " nao reconhecido");
		}

		return result;
	}

	protected AbstractRegistroAihInternacao(
			final AttributeFormatPairRendererHelper rendererHelper) {

		super(
				NomeStringTemplateEnum.INTERNACAO, rendererHelper);

	}

	@Override
	public NomeAtributoEnum[] getAtributos() {

		return NomeAtributoTemplate.values();
	}
}
