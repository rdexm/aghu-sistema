package br.gov.mec.aghu.faturamento.stringtemplate.impl;

import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihEnderecoPaciente;
import br.gov.mec.aghu.faturamento.stringtemplate.renderer.AttributeFormatPairRendererHelper;

public abstract class AbstractRegistroAihEnderecoPaciente
		extends
			AbstractRegistroAih
		implements
			RegistroAihEnderecoPaciente {

	@Override
	protected Object getAtributo(final NomeAtributoEnum atributo) {

		Object result = null;

		if (NomeAtributoTemplate.TP_LOGRADOURO.equals(atributo)) {
			result = this.getTpLogradouro();
		} else if (NomeAtributoTemplate.LOGR_PAC.equals(atributo)) {
			result = this.getLogrPac();
		} else if (NomeAtributoTemplate.NU_END_PAC.equals(atributo)) {
			result = this.getNuEndPac();
		} else if (NomeAtributoTemplate.COMPL_END_PAC.equals(atributo)) {
			result = this.getComplEndPac();
		} else if (NomeAtributoTemplate.BAIRRO_PAC.equals(atributo)) {
			result = this.getBairroPac();
		} else if (NomeAtributoTemplate.COD_MUN_END_PAC.equals(atributo)) {
			result = this.getCodMunEndPac();
		} else if (NomeAtributoTemplate.UF_PAC.equals(atributo)) {
			result = this.getUfPac();
		} else if (NomeAtributoTemplate.CEP_PAC.equals(atributo)) {
			result = this.getCepPac();
		} else {
			throw new IllegalArgumentException("Atributo: " + atributo
					+ " nao reconhecido");
		}

		return result;
	}

	protected AbstractRegistroAihEnderecoPaciente(
			final AttributeFormatPairRendererHelper rendererHelper) {

		super(
				NomeStringTemplateEnum.END_PACIENTE, rendererHelper);

	}

	@Override
	public NomeAtributoEnum[] getAtributos() {

		return NomeAtributoTemplate.values();
	}
}
