package br.gov.mec.aghu.faturamento.stringtemplate.impl;

import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihParto;
import br.gov.mec.aghu.faturamento.stringtemplate.renderer.AttributeFormatPairRendererHelper;

public abstract class AbstractRegistroAihParto
		extends
			AbstractRegistroAih
		implements
			RegistroAihParto {

	@Override
	protected Object getAtributo(final NomeAtributoEnum atributo) {

		Object result = null;

		if (NomeAtributoTemplate.QT_VIVOS.equals(atributo)) {
			result = this.getQtVivos();
		} else if (NomeAtributoTemplate.QT_MORTOS.equals(atributo)) {
			result = this.getQtMortos();
		} else if (NomeAtributoTemplate.QT_ALTA.equals(atributo)) {
			result = this.getQtAlta();
		} else if (NomeAtributoTemplate.QT_TRANSF.equals(atributo)) {
			result = this.getQtTransf();
		} else if (NomeAtributoTemplate.QT_OBITO.equals(atributo)) {
			result = this.getQtObito();
		} else if (NomeAtributoTemplate.QT_FILHOS.equals(atributo)) {
			result = this.getQtFilhos();
		} else if (NomeAtributoTemplate.GRAU_INSTRU.equals(atributo)) {
			result = this.getGrauInstru();
		} else if (NomeAtributoTemplate.CID_INDICACAO.equals(atributo)) {
			result = this.getCidIndicacao();
		} else if (NomeAtributoTemplate.TP_CONTRACEP1.equals(atributo)) {
			result = this.getTpContracep1();
		} else if (NomeAtributoTemplate.TP_CONTRACEP2.equals(atributo)) {
			result = this.getTpContracep2();
		} else if (NomeAtributoTemplate.ST_GESTRISCO.equals(atributo)) {
			result = this.getStGestrisco();
		} else if (NomeAtributoTemplate.NU_PRENATAL.equals(atributo)) {
			result = this.getNuPrenatal();
		} else {
			throw new IllegalArgumentException("Atributo: " + atributo
					+ " nao reconhecido");
		}

		return result;
	}

	protected AbstractRegistroAihParto(
			final AttributeFormatPairRendererHelper rendererHelper) {

		super(
				NomeStringTemplateEnum.PARTO, rendererHelper);

	}

	@Override
	public NomeAtributoEnum[] getAtributos() {

		return NomeAtributoTemplate.values();
	}
}
