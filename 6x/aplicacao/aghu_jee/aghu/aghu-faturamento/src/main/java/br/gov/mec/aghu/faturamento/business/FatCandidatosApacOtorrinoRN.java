package br.gov.mec.aghu.faturamento.business;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.FatCandidatosApacOtorrino;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


@Stateless
public class FatCandidatosApacOtorrinoRN extends BaseBusiness implements
		Serializable {

	private static final Log LOG = LogFactory.getLog(FatCandidatosApacOtorrinoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4195808772600550156L;


	/**
	 * ORADB Trigger FATT_CAOT_BRU
	 * 
	 * @param candidatosApacOtorrino
	 * @throws ApplicationBusinessException  
	 */
	public void executarAntesAtualizarFatCandidatosApacOtorrino(FatCandidatosApacOtorrino candidatosApacOtorrinoNew, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		candidatosApacOtorrinoNew.setAlteradoEm(new Date());
		candidatosApacOtorrinoNew.setServidorAlterado(servidorLogado);		
	}	

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
