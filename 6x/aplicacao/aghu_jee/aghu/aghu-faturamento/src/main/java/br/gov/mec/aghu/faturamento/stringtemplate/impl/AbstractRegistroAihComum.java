package br.gov.mec.aghu.faturamento.stringtemplate.impl;

import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihComum;
import br.gov.mec.aghu.faturamento.stringtemplate.renderer.AttributeFormatPairRendererHelper;

public abstract class AbstractRegistroAihComum
		extends
			AbstractRegistroAih
		implements
			RegistroAihComum {

	@Override
	protected Object getAtributo(final NomeAtributoEnum atributo) {

		Object result = null;

		if (NomeAtributoTemplate.NU_LOTE.equals(atributo)) {
			result = this.getNuLote();
		} else if (NomeAtributoTemplate.QT_LOTE.equals(atributo)) {
			result = this.getQtLote();
		} else if (NomeAtributoTemplate.APRES_LOTE.equals(atributo)) {
			result = this.getApresLote();
		} else if (NomeAtributoTemplate.SEQ_LOTE.equals(atributo)) {
			result = this.getSeqLote();
		} else if (NomeAtributoTemplate.ORG_EMIS_AIH.equals(atributo)) {
			result = this.getOrgEmisAih();
		} else if (NomeAtributoTemplate.CNES_HOSP.equals(atributo)) {
			result = this.getCnesHosp();
		} else if (NomeAtributoTemplate.MUN_HOSP.equals(atributo)) {
			result = this.getMunHosp();
		} else if (NomeAtributoTemplate.NU_AIH.equals(atributo)) {
			result = this.getNuAih();
		} else if (NomeAtributoTemplate.IDENT_AIH.equals(atributo)) {
			result = this.getIdentAih();
		} else if (NomeAtributoTemplate.ESPEC_AIH.equals(atributo)) {
			result = this.getEspecAih();
		} else {
			throw new IllegalArgumentException("Atributo: " + atributo
					+ " nao reconhecido");
		}

		return result;
	}

	protected AbstractRegistroAihComum(
			final AttributeFormatPairRendererHelper rendererHelper) {

		super(
				NomeStringTemplateEnum.COMUM, rendererHelper);

	}

	@Override
	public NomeAtributoEnum[] getAtributos() {

		return NomeAtributoTemplate.values();
	}
}
