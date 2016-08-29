package br.gov.mec.aghu.estoque.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.dao.SceLoteDocImpressaoDAO;
import br.gov.mec.aghu.model.SceLoteDocImpressao;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@Stateless
public class SceLoteDocImpressaoRN extends BaseBusiness{


@Inject
private SceLoteDocImpressaoDAO sceLoteDocImpressaoDAO;

	private static final Log LOG = LogFactory.getLog(SceLoteDocImpressaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	private static final long serialVersionUID = -2675662856295022980L;

	public enum SceLoteDocImpressaoRNExceptionCode implements BusinessExceptionCode {
		MSG_ERR0_USUARIO_NAO_CADASTRADO;

		public void throwException(Object... params) throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
		
		public void throwException(Throwable cause, Object... params) throws ApplicationBusinessException {
			// Tratamento adicional para não esconder a exceção de negocio original
			if (cause instanceof ApplicationBusinessException) {
				throw (ApplicationBusinessException) cause;
			}
			throw new ApplicationBusinessException(this, cause, params);
		}

	}

	public void atualizar(SceLoteDocImpressao loteDocImpressao) throws BaseException{

		//	preAtualizar(loteDocumento);
		getSceLoteDocImpressaoDAO().atualizar(loteDocImpressao);
	}

	protected SceLoteDocImpressaoDAO getSceLoteDocImpressaoDAO() {
		return sceLoteDocImpressaoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}