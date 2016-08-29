package br.gov.mec.aghu.exames.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.dao.AelExtratoItemCartasDAO;
import br.gov.mec.aghu.model.AelExtratoItemCartas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author lucas
 *
 */
@Stateless
public class AelExtratoItemCartasRN extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(AelExtratoItemCartasRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AelExtratoItemCartasDAO aelExtratoItemCartasDAO;
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -447988638606290994L;

	public void inserirAelExtratoItemCartas(AelExtratoItemCartas aelExtratoItemCartas, Boolean flush) throws ApplicationBusinessException {
		
		beforeInsertAelExtratoItemCartas(aelExtratoItemCartas);
		getAelExtratoItemCartasDAO().persistir(aelExtratoItemCartas);
		if (flush){
			getAelExtratoItemCartasDAO().flush();
		}
		
	}
	
	/**
	 * ORADB TRIGGER AELT_EAR_BRI
	 * @param aelItemSolicCartas
	 * @throws ApplicationBusinessException 
	 *  
	 */
	public void beforeInsertAelExtratoItemCartas(AelExtratoItemCartas aelExtratoItemCartas) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelExtratoItemCartas.setDthrEvento(new Date());
		aelExtratoItemCartas.setServidor(servidorLogado);
	}
	

	protected AelExtratoItemCartasDAO getAelExtratoItemCartasDAO() {
		return aelExtratoItemCartasDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
