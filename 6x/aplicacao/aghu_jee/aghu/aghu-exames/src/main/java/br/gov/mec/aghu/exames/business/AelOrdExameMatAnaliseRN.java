package br.gov.mec.aghu.exames.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelOrdExameMatAnaliseDAO;
import br.gov.mec.aghu.model.AelOrdExameMatAnalise;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class AelOrdExameMatAnaliseRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelOrdExameMatAnaliseRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelOrdExameMatAnaliseDAO aelOrdExameMatAnaliseDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7044145069397692683L;
	public static final Short ordemNivel1 = Short.valueOf("99");
	public static final Short ordemNivel2 = Short.valueOf("0");
	
	/**
	 * ORADB TRIGGER AELT_OEM_BRI
	 * @param aelCopiaResultados
	 * @throws ApplicationBusinessException  
	 */
	public void inserirAelOrdExameMatAnalise(AelOrdExameMatAnalise aelOrdExameMatAnalise) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelOrdExameMatAnalise.setServidor(servidorLogado);
		
		if (aelOrdExameMatAnalise.getOrdemNivel1() == null) {
			
			aelOrdExameMatAnalise.setOrdemNivel1(ordemNivel1);
			
		}
		
		if (aelOrdExameMatAnalise.getOrdemNivel2() == null) {
			
			aelOrdExameMatAnalise.setOrdemNivel2(ordemNivel2);
			
		}
		
		aelOrdExameMatAnalise.setCriadoEm(new Date());
		
		getAelOrdExameMatAnaliseDAO().persistir(aelOrdExameMatAnalise);
		getAelOrdExameMatAnaliseDAO().flush();
		
	}
	
	/**
	 * ORADB TRIGGER AELT_OEM_BRI
	 * ORADB TRIGGER AELT_OEM_BRU
	 */
	public void atualizarAelOrdExameMatAnalise(AelOrdExameMatAnalise aelOrdExameMatAnalise) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelOrdExameMatAnalise.setServidor(servidorLogado);
		getAelOrdExameMatAnaliseDAO().merge(aelOrdExameMatAnalise);
	}
	
	private AelOrdExameMatAnaliseDAO getAelOrdExameMatAnaliseDAO() {
		return aelOrdExameMatAnaliseDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
