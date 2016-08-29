package br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.EpeCaractDefinidora;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeCaractDefDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeCaractDefinidoraDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class SinaisSintomasRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(SinaisSintomasRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private EpeCaractDefDiagnosticoDAO epeCaractDefDiagnosticoDAO;

@Inject
private EpeCaractDefinidoraDAO epeCaractDefinidoraDAO;	

	private static final long serialVersionUID = 2969601492841136788L;
	
	public enum SinaisSintomasRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SINAIS_SINTOMAS_ERRO_ALTERACAO
	}

	/**
	 * #4871 RN1 RN2 RN3
	 * @ORADB EPET_CDE_BRI
	 * Desnecessária sendo resolvida naturalmente ao longo da aplicação...
	 */
	
	/**
	 * #4871 RN4 RN5 RN6
	 * @ORADB EPET_CDE_BRU (trigger)
	 * @ORADB RN_CDEP_VER_CRD (procedure)
	 */
	public void verificaAlteracaoEpeCaractDefinidora(EpeCaractDefinidora epeCaractDefinidora) throws ApplicationBusinessException {
		if (!getEpeCaractDefinidoraDAO().obterOriginal(epeCaractDefinidora).getSituacao().equals(epeCaractDefinidora.getSituacao())) {
			if (getEpeCaractDefDiagnosticoDAO().possuiEpeCaractDefinidora(epeCaractDefinidora)) {
				throw new ApplicationBusinessException(SinaisSintomasRNExceptionCode.MENSAGEM_SINAIS_SINTOMAS_ERRO_ALTERACAO);
			}
		}
		
	}
	
	protected EpeCaractDefDiagnosticoDAO getEpeCaractDefDiagnosticoDAO() {
		return epeCaractDefDiagnosticoDAO;
	}
	
	protected EpeCaractDefinidoraDAO getEpeCaractDefinidoraDAO() {
		return epeCaractDefinidoraDAO;
	}
	
}
