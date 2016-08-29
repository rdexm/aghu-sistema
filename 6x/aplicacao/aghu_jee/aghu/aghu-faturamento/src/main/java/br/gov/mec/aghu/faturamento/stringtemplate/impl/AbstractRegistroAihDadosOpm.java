package br.gov.mec.aghu.faturamento.stringtemplate.impl;

import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihDadosOpm;
import br.gov.mec.aghu.faturamento.stringtemplate.renderer.AttributeFormatPairRendererHelper;

public abstract class AbstractRegistroAihDadosOpm
		extends
			AbstractRegistroAih
		implements
			RegistroAihDadosOpm {

	@Override
	protected Object getAtributo(final NomeAtributoEnum atributo) {

		Object result = null;

		if (NomeAtributoTemplate.COD_OPM.equals(atributo)) {
			result = this.getCodOpm();
		} else if (NomeAtributoTemplate.LINHA.equals(atributo)) {
			result = this.getLinha();
		} else if (NomeAtributoTemplate.REG_ANVISA.equals(atributo)) {
			result = this.getRegAnvisa();
		} else if (NomeAtributoTemplate.SERIE.equals(atributo)) {
			result = this.getSerie();
		} else if (NomeAtributoTemplate.LOTE.equals(atributo)) {
			result = this.getLote();
		} else if (NomeAtributoTemplate.NOTA_FISCAL.equals(atributo)) {
			result = this.getNotaFiscal();
		} else if (NomeAtributoTemplate.CNPJ_FORN.equals(atributo)) {
			result = this.getCnpjForn();
		} else if (NomeAtributoTemplate.CNPJ_FABRIC.equals(atributo)) {
			result = this.getCnpjFabric();
		} else {
			throw new IllegalArgumentException("Atributo: " + atributo
					+ " nao reconhecido");
		}

		return result;
	}

	protected AbstractRegistroAihDadosOpm(
			final AttributeFormatPairRendererHelper rendererHelper) {

		super(
				NomeStringTemplateEnum.DADOS_OPM, rendererHelper);

	}

	@Override
	public NomeAtributoEnum[] getAtributos() {

		return NomeAtributoTemplate.values();
	}
}
