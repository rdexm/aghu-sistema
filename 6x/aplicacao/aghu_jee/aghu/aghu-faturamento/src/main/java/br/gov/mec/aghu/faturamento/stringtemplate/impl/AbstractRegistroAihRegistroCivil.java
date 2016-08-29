package br.gov.mec.aghu.faturamento.stringtemplate.impl;

import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihRegistroCivil;
import br.gov.mec.aghu.faturamento.stringtemplate.renderer.AttributeFormatPairRendererHelper;

public abstract class AbstractRegistroAihRegistroCivil
		extends
			AbstractRegistroAih
		implements
			RegistroAihRegistroCivil {

	@Override
	protected Object getAtributo(final NomeAtributoEnum atributo) {

		Object result = null;

		if (NomeAtributoTemplate.NUMERO_DN.equals(atributo)) {
			result = this.getNumeroDn();
		} else if (NomeAtributoTemplate.NOME_RN.equals(atributo)) {
			result = this.getNomeRn();
		} else if (NomeAtributoTemplate.RS_CART.equals(atributo)) {
			result = this.getRsCart();
		} else if (NomeAtributoTemplate.LIVRO_RN.equals(atributo)) {
			result = this.getLivroRn();
		} else if (NomeAtributoTemplate.FOLHA_RN.equals(atributo)) {
			result = this.getFolhaRn();
		} else if (NomeAtributoTemplate.TERMO_RN.equals(atributo)) {
			result = this.getTermoRn();
		} else if (NomeAtributoTemplate.DT_EMIS_RN.equals(atributo)) {
			result = this.getDtEmisRn();
		} else if (NomeAtributoTemplate.LINHA.equals(atributo)) {
			result = this.getLinha();
		} else if (NomeAtributoTemplate.MATRICULA.equals(atributo)) {
			result = this.getMatricula();
		} else {
			throw new IllegalArgumentException("Atributo: " + atributo
					+ " nao reconhecido");
		}

		return result;
	}

	protected AbstractRegistroAihRegistroCivil(
			final AttributeFormatPairRendererHelper rendererHelper) {

		super(
				NomeStringTemplateEnum.REG_CIVIL, rendererHelper);

	}

	@Override
	public NomeAtributoEnum[] getAtributos() {

		return NomeAtributoTemplate.values();
	}
}
