package br.gov.mec.aghu.faturamento.stringtemplate.impl;

import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihEspecificacao;
import br.gov.mec.aghu.faturamento.stringtemplate.renderer.AttributeFormatPairRendererHelper;

public abstract class AbstractRegistroAihEspecificacao
		extends
			AbstractRegistroAih
		implements
			RegistroAihEspecificacao {

	@Override
	protected Object getAtributo(final NomeAtributoEnum atributo) {

		Object result = null;

		if (NomeAtributoTemplate.MOD_INTERN.equals(atributo)) {
			result = this.getModIntern();
		} else if (NomeAtributoTemplate.SEQ_AIH5.equals(atributo)) {
			result = this.getSeqAih5();
		} else if (NomeAtributoTemplate.AIH_PROX.equals(atributo)) {
			result = this.getAihProx();
		} else if (NomeAtributoTemplate.AIH_ANT.equals(atributo)) {
			result = this.getAihAnt();
		} else if (NomeAtributoTemplate.DT_EMISSAO.equals(atributo)) {
			result = this.getDtEmissao();
		} else if (NomeAtributoTemplate.DT_INTERN.equals(atributo)) {
			result = this.getDtIntern();
		} else if (NomeAtributoTemplate.DT_SAIDA.equals(atributo)) {
			result = this.getDtSaida();
		} else if (NomeAtributoTemplate.PROC_SOLICITADO.equals(atributo)) {
			result = this.getProcSolicitado();
		} else if (NomeAtributoTemplate.ST_MUDAPROC.equals(atributo)) {
			result = this.getStMudaproc();
		} else if (NomeAtributoTemplate.PROC_REALIZADO.equals(atributo)) {
			result = this.getProcRealizado();
		} else if (NomeAtributoTemplate.CAR_INTERN.equals(atributo)) {
			result = this.getCarIntern();
		} else if (NomeAtributoTemplate.MOT_SAIDA.equals(atributo)) {
			result = this.getMotSaida();
		} else if (NomeAtributoTemplate.IDENT_MED_SOL.equals(atributo)) {
			result = this.getIdentMedSol();
		} else if (NomeAtributoTemplate.DOC_MED_SOL.equals(atributo)) {
			result = this.getDocMedSol();
		} else if (NomeAtributoTemplate.IDENT_MED_RESP.equals(atributo)) {
			result = this.getIdentMedResp();
		} else if (NomeAtributoTemplate.DOC_MED_RESP.equals(atributo)) {
			result = this.getDocMedResp();
		} else if (NomeAtributoTemplate.IDENT_DIRCLINICO.equals(atributo)) {
			result = this.getIdentDirclinico();
		} else if (NomeAtributoTemplate.DOC_DIRCLINICO.equals(atributo)) {
			result = this.getDocDirclinico();
		} else if (NomeAtributoTemplate.IDENT_AUTORIZ.equals(atributo)) {
			result = this.getIdentAutoriz();
		} else if (NomeAtributoTemplate.DOC_AUTORIZ.equals(atributo)) {
			result = this.getDocAutoriz();
		} else if (NomeAtributoTemplate.DIAG_PRIN.equals(atributo)) {
			result = this.getDiagPrin();
		} else if (NomeAtributoTemplate.DIAG_SEC.equals(atributo)) {
			result = this.getDiagSec();
		} else if (NomeAtributoTemplate.DIAG_COMPL.equals(atributo)) {
			result = this.getDiagCompl();
		} else if (NomeAtributoTemplate.DIAG_OBITO.equals(atributo)) {
			result = this.getDiagObito();
		} else if (NomeAtributoTemplate.COD_SOL_LIB.equals(atributo)) {
			result = this.getCodSolLib();
		} else {
			throw new IllegalArgumentException("Atributo: " + atributo
					+ " nao reconhecido");
		}

		return result;
	}

	protected AbstractRegistroAihEspecificacao(
			final AttributeFormatPairRendererHelper rendererHelper) {

		super(
				NomeStringTemplateEnum.ESPECIFICACAO, rendererHelper);

	}

	@Override
	public NomeAtributoEnum[] getAtributos() {

		return NomeAtributoTemplate.values();
	}
}
