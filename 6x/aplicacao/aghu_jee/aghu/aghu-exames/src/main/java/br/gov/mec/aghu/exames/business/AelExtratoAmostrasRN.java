package br.gov.mec.aghu.exames.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.dao.AelExtratoAmostrasDAO;
import br.gov.mec.aghu.model.AelExtratoAmostras;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * 
 * @author lucas
 *
 */
@Stateless
public class AelExtratoAmostrasRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelExtratoAmostrasRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelExtratoAmostrasDAO aelExtratoAmostrasDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2030585229617660641L;

	public void inserirAelExtratoAmostra(AelExtratoAmostras aelExtratoAmostras) throws BaseException {
		beforeInsertAelExtratoAmostras(aelExtratoAmostras);			
		getAelExtratoAmostrasDAO().persistir(aelExtratoAmostras);
		getAelExtratoAmostrasDAO().flush();
	}
	
	/**
	 * ORADB TRIGGER AELT_EXM_BRI
	 * @param aelExtratoAmostras
	 * @throws ApplicationBusinessException 
	 *  
	 */
	public void beforeInsertAelExtratoAmostras(AelExtratoAmostras aelExtratoAmostras) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelExtratoAmostras.setCriadoEm(new Date());
		aelExtratoAmostras.setServidor(servidorLogado);
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	public AelExtratoAmostrasDAO getAelExtratoAmostrasDAO(){
		return aelExtratoAmostrasDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
