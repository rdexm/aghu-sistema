package br.gov.mec.aghu.faturamento.stringtemplate.impl;

import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihAcidenteTrabalho;
import br.gov.mec.aghu.faturamento.stringtemplate.renderer.AttributeFormatPairRendererHelper;

public abstract class AbstractRegistroAihAcidenteTrabalho
		extends
			AbstractRegistroAih
		implements
			RegistroAihAcidenteTrabalho {

	@Override
	protected Object getAtributo(final NomeAtributoEnum atributo) {

		Object result = null;

		if (NomeAtributoTemplate.CNPJ_EMPREG.equals(atributo)) {
			result = this.getCnpjEmpreg();
		} else if (NomeAtributoTemplate.CBOR.equals(atributo)) {
			result = this.getCbor();
		} else if (NomeAtributoTemplate.CNAER.equals(atributo)) {
			result = this.getCnaer();
		} else if (NomeAtributoTemplate.TP_VINCPREV.equals(atributo)) {
			result = this.getTpVincprev();
		} else {
			throw new IllegalArgumentException("Atributo: " + atributo
					+ " nao reconhecido");
		}

		return result;
	}

	protected AbstractRegistroAihAcidenteTrabalho(
			final AttributeFormatPairRendererHelper rendererHelper) {

		super(
				NomeStringTemplateEnum.ACIDENTE_TRABALHO, rendererHelper);
	}

	@Override
	public NomeAtributoEnum[] getAtributos() {

		return NomeAtributoTemplate.values();
	}
}
