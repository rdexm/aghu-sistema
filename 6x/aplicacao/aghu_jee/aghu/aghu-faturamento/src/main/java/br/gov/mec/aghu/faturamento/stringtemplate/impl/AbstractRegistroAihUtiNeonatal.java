package br.gov.mec.aghu.faturamento.stringtemplate.impl;

import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihUtiNeonatal;
import br.gov.mec.aghu.faturamento.stringtemplate.renderer.AttributeFormatPairRendererHelper;

public abstract class AbstractRegistroAihUtiNeonatal
		extends
			AbstractRegistroAih
		implements
			RegistroAihUtiNeonatal {

	@Override
	protected Object getAtributo(final NomeAtributoEnum atributo) {

		Object result = null;

		if (NomeAtributoTemplate.SAIDA_UTINEO.equals(atributo)) {
			result = this.getSaidaUtineo();
		} else if (NomeAtributoTemplate.PESO_UTINEO.equals(atributo)) {
			result = this.getPesoUtineo();
		} else if (NomeAtributoTemplate.MESGEST_UTINEO.equals(atributo)) {
			result = this.getMesgestUtineo();
		} else {
			throw new IllegalArgumentException("Atributo: " + atributo
					+ " nao reconhecido");
		}

		return result;
	}

	protected AbstractRegistroAihUtiNeonatal(
			final AttributeFormatPairRendererHelper rendererHelper) {

		super(
				NomeStringTemplateEnum.UTI_NEONATAL, rendererHelper);
	}

	@Override
	public NomeAtributoEnum[] getAtributos() {

		return NomeAtributoTemplate.values();
	}
}
