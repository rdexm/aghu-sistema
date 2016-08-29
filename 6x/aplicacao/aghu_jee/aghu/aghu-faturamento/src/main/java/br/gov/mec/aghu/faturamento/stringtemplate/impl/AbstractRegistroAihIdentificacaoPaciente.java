package br.gov.mec.aghu.faturamento.stringtemplate.impl;

import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihIdentificacaoPaciente;
import br.gov.mec.aghu.faturamento.stringtemplate.renderer.AttributeFormatPairRendererHelper;

public abstract class AbstractRegistroAihIdentificacaoPaciente
		extends
			AbstractRegistroAih
		implements
			RegistroAihIdentificacaoPaciente {

	@Override
	protected Object getAtributo(final NomeAtributoEnum atributo) {

		Object result = null;

		if (NomeAtributoTemplate.NM_PACIENTE.equals(atributo)) {
			result = this.getNmPaciente();
		} else if (NomeAtributoTemplate.DT_NASC_PAC.equals(atributo)) {
			result = this.getDtNascPac();
		} else if (NomeAtributoTemplate.SEXO_PAC.equals(atributo)) {
			result = this.getSexoPac();
		} else if (NomeAtributoTemplate.RACA_COR.equals(atributo)) {
			result = this.getRacaCor();
		} else if (NomeAtributoTemplate.NM_MAE_PAC.equals(atributo)) {
			result = this.getNmMaePac();
		} else if (NomeAtributoTemplate.NM_RESP_PAC.equals(atributo)) {
			result = this.getNmRespPac();
		} else if (NomeAtributoTemplate.TP_DOC_PAC.equals(atributo)) {
			result = this.getTpDocPac();
		} else if (NomeAtributoTemplate.ETNIA_INDIGENA.equals(atributo)) {
			result = this.getEtniaIndigena();
		} else if (NomeAtributoTemplate.NU_CNS.equals(atributo)) {
			result = this.getNuCns();
		} else if (NomeAtributoTemplate.NAC_PAC.equals(atributo)) {
			result = this.getNacPac();
		} else {
			throw new IllegalArgumentException("Atributo: " + atributo
					+ " nao reconhecido");
		}

		return result;
	}

	protected AbstractRegistroAihIdentificacaoPaciente(
			final AttributeFormatPairRendererHelper rendererHelper) {

		super(
				NomeStringTemplateEnum.IDENT_PACIENTE, rendererHelper);
	}

	@Override
	public NomeAtributoEnum[] getAtributos() {

		return NomeAtributoTemplate.values();
	}
}
