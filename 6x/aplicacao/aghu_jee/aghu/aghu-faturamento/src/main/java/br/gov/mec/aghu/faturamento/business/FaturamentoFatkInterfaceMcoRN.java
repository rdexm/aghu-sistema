package br.gov.mec.aghu.faturamento.business;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * ORADB FATK_INTERFACE_MCO
 *
 */
@Stateless
public class FaturamentoFatkInterfaceMcoRN extends BaseBusiness implements Serializable {
	
	private static final Log LOG = LogFactory.getLog(FaturamentoFatkInterfaceMcoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	@EJB
	private IAghuFacade aghuFacade;
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 6837919765097920412L;

	/**
	 * ORADB Procedure FATK_INTERFACE_MCO.RN_FATP_INS_CENTRO_OBSTETRICO
	 *  
	 * 
	 */
	public void rnFatpInsCentroObstetrico(final Integer pAtdSeq
			 ,final Integer pCth
			 ,final Date pDtInter
			 ,final Date pDtAlta
			 ,final Integer pOpcao) throws ApplicationBusinessException {

	
//		AghAtendimentos aghAtend = aghAtendDAO.obterPorChavePrimaria(pAtdSeq);
		
//		throw new NotImplementedException("Este método ainda não foi implementado"); 
		getAghuFacade().inserirCentroObstetrico(pAtdSeq, pCth, pDtInter, pDtAlta, pOpcao);
		
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
}
