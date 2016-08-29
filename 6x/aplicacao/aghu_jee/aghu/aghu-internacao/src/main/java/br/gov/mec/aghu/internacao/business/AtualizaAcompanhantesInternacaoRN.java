package br.gov.mec.aghu.internacao.business;

import java.util.List;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AinAcompanhantesInternacao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * RN para a estoria de usuario 'Atualizar Acompanhantes da Internacao'.
 */
@Stateless
public class AtualizaAcompanhantesInternacaoRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AtualizaAcompanhantesInternacaoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


	private static final long serialVersionUID = -183604377847437382L;

	private enum AtualizaAcompanhantesInternacaoONExceptionCode implements
			BusinessExceptionCode {
		RAP_00904_OUTRO_ACOMPANHANTE_SEM_DT_FIM, RAP_00175_SEM_SERVIDOR_CADASTRADO;
	}

	/**
	 * ORADB Trigger AINT_ACI_BRI / AINT_ACI_BRU.
	 */
	public void executarAintAciBriBru(
			AinAcompanhantesInternacao acompanhanteInternacao,
			List<AinAcompanhantesInternacao> acompanhantesInternacaos)
			throws ApplicationBusinessException {

		if (acompanhanteInternacao.getDataHoraFim() == null
				&& hasDtFimVazia(acompanhanteInternacao,
						acompanhantesInternacaos)) {
			throw new ApplicationBusinessException(
					AtualizaAcompanhantesInternacaoONExceptionCode.RAP_00904_OUTRO_ACOMPANHANTE_SEM_DT_FIM);
		}

		if (acompanhanteInternacao.getServidor() == null) {
			throw new ApplicationBusinessException(
					AtualizaAcompanhantesInternacaoONExceptionCode.RAP_00175_SEM_SERVIDOR_CADASTRADO);
		}
	}

	/**
	 * Verifica se já existe algum acompanhante com data fim nulo.
	 * 
	 * @param acompanhanteInternacao
	 */
	private Boolean hasDtFimVazia(
			AinAcompanhantesInternacao acompanhanteInternacao,
			List<AinAcompanhantesInternacao> acompanhantesInternacao) {

		for (AinAcompanhantesInternacao a : acompanhantesInternacao) {
			if (a.getDataHoraFim() == null && !a.equals(acompanhanteInternacao)) {
				return true;
			}
		}

		return false;
	}
}
