package br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeCuidados;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescCuidDiagnosticoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class CuidadoDiagnosticoRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(CuidadoDiagnosticoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private EpePrescCuidDiagnosticoDAO epePrescCuidDiagnosticoDAO;

	private static final long serialVersionUID = 1925619591771334385L;

	public enum DiagnosticoRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_ADICAO_MANTER_DIAGNOSTICO_CUIDADO, MENSAGEM_ERRO_EXCLUSAO_MANTER_DIAGNOSTICO_CUIDADO
	}

	/**
	 * @throws ApplicationBusinessException
	 *             #4960 RN1
	 */
	public void prePersistirValidarEpeCuidados(EpeCuidados cuidado) throws ApplicationBusinessException {
		if (!DominioSituacao.A.equals(cuidado.getIndSituacao())) {
			throw new ApplicationBusinessException(DiagnosticoRNExceptionCode.MENSAGEM_ERRO_ADICAO_MANTER_DIAGNOSTICO_CUIDADO);
		}
	}

	/**
	 * @throws ApplicationBusinessException
	 *             #4960 RN2
	 */
	public void preRemoverValidarCuidadoRelacionadoPrescricao(Short cuiSeq, Short freSeq, Short dgnSequencia, Short dgnSnbSequencia, Short dgnSnbGnbSeq)
			throws ApplicationBusinessException {
		if (getEpePrescCuidDiagnosticoDAO().verificarCuidadoRelacionadoPrescricao(cuiSeq, freSeq, dgnSequencia, dgnSnbSequencia, dgnSnbGnbSeq)) {
			throw new ApplicationBusinessException(DiagnosticoRNExceptionCode.MENSAGEM_ERRO_EXCLUSAO_MANTER_DIAGNOSTICO_CUIDADO);
		}
	}

	protected EpePrescCuidDiagnosticoDAO getEpePrescCuidDiagnosticoDAO() {
		return epePrescCuidDiagnosticoDAO;
	}

}
