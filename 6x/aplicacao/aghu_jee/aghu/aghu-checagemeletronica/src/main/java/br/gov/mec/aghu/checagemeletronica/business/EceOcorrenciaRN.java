package br.gov.mec.aghu.checagemeletronica.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.checagemeletronica.dao.EceOcorrenciaDAO;
import br.gov.mec.aghu.model.EceOcorrencia;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class EceOcorrenciaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(EceOcorrenciaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	
	@Inject
	private EceOcorrenciaDAO eceOcorrenciaDAO;

	private static final long serialVersionUID = -273532371591508065L;

	/**
	 * @ORADB ECET_OCO_BRI
	 * 
	 * @param eceOcorrencia
	 * @throws ApplicationBusinessException  
	 */
	@SuppressWarnings("ucd")
	public void inserirEceOcorrencia(final EceOcorrencia eceOcorrencia) throws ApplicationBusinessException {
		final EceOcorrenciaDAO eceOcorrenciaDAO = getEceOcorrenciaDAO();
		
		this.executarAntesInserir(eceOcorrencia);
		eceOcorrenciaDAO.persistir(eceOcorrencia);
		
	}
	
	private void executarAntesInserir(final EceOcorrencia eceOcorrencia) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		eceOcorrencia.setRapServidores(servidorLogado);
		eceOcorrencia.setCriadoEm(new Date());
		eceOcorrencia.setAlteradoEm(new Date());
	}

	/**
	 * @ORADB ECET_OCO_BRU
	 * 
	 * @param eceOcorrencia
	 */
	public void alterarOrdemLocalizacao(final EceOcorrencia eceOcorrencia) {
		this.executarAntesAlterar(eceOcorrencia);
		eceOcorrenciaDAO.atualizar(eceOcorrencia);
	}

	private void executarAntesAlterar(final EceOcorrencia eceOcorrencia) {
		eceOcorrencia.setAlteradoEm(new Date());
	}

	protected EceOcorrenciaDAO getEceOcorrenciaDAO() {
		return eceOcorrenciaDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
