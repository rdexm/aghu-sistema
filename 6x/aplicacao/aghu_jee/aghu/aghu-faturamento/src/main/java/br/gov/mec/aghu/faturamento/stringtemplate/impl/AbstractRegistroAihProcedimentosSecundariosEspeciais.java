package br.gov.mec.aghu.faturamento.stringtemplate.impl;

import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihProcedimentosSecundariosEspeciais;
import br.gov.mec.aghu.faturamento.stringtemplate.renderer.AttributeFormatPairRendererHelper;

public abstract class AbstractRegistroAihProcedimentosSecundariosEspeciais
		extends
			AbstractRegistroAih
		implements
			RegistroAihProcedimentosSecundariosEspeciais {

	@Override
	protected Object getAtributo(final NomeAtributoEnum atributo) {

		Object result = null;

		if (NomeAtributoTemplate.IN_PROF.equals(atributo)) {
			result = this.getInProf();
		} else if (NomeAtributoTemplate.IDENT_PROF.equals(atributo)) {
			result = this.getIdentProf();
		} else if (NomeAtributoTemplate.CBO_PROF.equals(atributo)) {
			result = this.getCboProf();
		} else if (NomeAtributoTemplate.IN_EQUIPE.equals(atributo)) {
			result = this.getInEquipe();
		} else if (NomeAtributoTemplate.IN_SERVICO.equals(atributo)) {
			result = this.getInServico();
		} else if (NomeAtributoTemplate.IDENT_SERVICO.equals(atributo)) {
			result = this.getIdentServico();
		} else if (NomeAtributoTemplate.IN_EXECUTOR.equals(atributo)) {
			result = this.getInExecutor();
		} else if (NomeAtributoTemplate.IDENT_EXECUTOR.equals(atributo)) {
			result = this.getIdentExecutor();
		} else if (NomeAtributoTemplate.COD_PROCED.equals(atributo)) {
			result = this.getCodProced();
		} else if (NomeAtributoTemplate.QTD_PROCED.equals(atributo)) {
			result = this.getQtdProced();
		} else if (NomeAtributoTemplate.CMPT.equals(atributo)) {
			result = this.getCmpt();
		} else {
			throw new IllegalArgumentException("Atributo: " + atributo
					+ " nao reconhecido");
		}

		return result;
	}

	protected AbstractRegistroAihProcedimentosSecundariosEspeciais(
			final AttributeFormatPairRendererHelper rendererHelper) {

		super(
				NomeStringTemplateEnum.PROC_SEC_ESP, rendererHelper);

	}

	@Override
	public NomeAtributoEnum[] getAtributos() {

		return NomeAtributoTemplate.values();
	}
}
