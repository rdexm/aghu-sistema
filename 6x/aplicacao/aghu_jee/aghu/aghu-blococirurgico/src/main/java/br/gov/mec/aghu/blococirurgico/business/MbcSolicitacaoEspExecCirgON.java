package br.gov.mec.aghu.blococirurgico.business;


import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.MbcSolicitacaoEspExecCirgDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcSolicitacaoEspExecCirg;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MbcSolicitacaoEspExecCirgON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcSolicitacaoEspExecCirgON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcSolicitacaoEspExecCirgDAO mbcSolicitacaoEspExecCirgDAO;


	@EJB
	private IParametroFacade iParametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1134948931921935499L;

	private enum MbcSolicitacaoEspExecCirgONExceptionCode implements BusinessExceptionCode {
		MBC_00449;
	}
	
	protected MbcSolicitacaoEspExecCirgDAO getMbcSolicitacaoEspExecCirgDAO() {
		return mbcSolicitacaoEspExecCirgDAO;
	}

	/**
	 * 
	 * Verifica a existência de MbcSolicitacaoEspExecCirg com necessidade cirúrgica já cadastrada para a cirurgia.
	 * 
	 * @param crgSeq
	 * @param nciSeq
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void verificarSolicEspExistente(Integer crgSeq, Short nciSeq) throws ApplicationBusinessException, ApplicationBusinessException {
		AghParametros parametro = getParametroFacade()
		.buscarAghParametro(AghuParametrosEnum.P_NESS_CIRG_DIVERSA);
		
		if (parametro != null && parametro.getVlrNumerico() != null) {
			Short nciSeqDiversa = parametro.getVlrNumerico().shortValue();
			
			MbcSolicitacaoEspExecCirg existente = getMbcSolicitacaoEspExecCirgDAO()
				.verificarSolicEspExistente(crgSeq, nciSeq, nciSeqDiversa);
			
			if (existente != null) {
				throw new ApplicationBusinessException(MbcSolicitacaoEspExecCirgONExceptionCode.MBC_00449);
			}
		}
		
	}

	protected IParametroFacade getParametroFacade(){
		return  iParametroFacade;
	}
}
