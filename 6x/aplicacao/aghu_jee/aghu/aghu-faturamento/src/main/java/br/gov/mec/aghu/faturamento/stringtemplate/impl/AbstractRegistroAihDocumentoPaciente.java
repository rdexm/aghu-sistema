package br.gov.mec.aghu.faturamento.stringtemplate.impl;

import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihDocumentoPaciente;
import br.gov.mec.aghu.faturamento.stringtemplate.renderer.AttributeFormatPairRendererHelper;

public abstract class AbstractRegistroAihDocumentoPaciente
		extends
			AbstractRegistroAih
		implements
			RegistroAihDocumentoPaciente {

	@Override
	protected Object getAtributo(final NomeAtributoEnum atributo) {

		Object result = null;

		if (NomeAtributoTemplate.NU_DOC_PAC.equals(atributo)) {
			result = this.getNuDocPac();
		} else {
			throw new IllegalArgumentException("Atributo: " + atributo
					+ " nao reconhecido");
		}

		return result;
	}

	protected AbstractRegistroAihDocumentoPaciente(
			final AttributeFormatPairRendererHelper rendererHelper) {

		super(
				NomeStringTemplateEnum.DOC_PACIENTE, rendererHelper);

	}

	@Override
	public NomeAtributoEnum[] getAtributos() {

		return NomeAtributoTemplate.values();
	}
}
