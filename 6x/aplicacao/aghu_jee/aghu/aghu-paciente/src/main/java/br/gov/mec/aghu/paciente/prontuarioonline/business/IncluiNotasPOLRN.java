package br.gov.mec.aghu.paciente.prontuarioonline.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class IncluiNotasPOLRN extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(IncluiNotasPOLRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IParametroFacade parametroFacade;


	private static final long serialVersionUID = 1190653317739174639L;

	/**
	 * @ORADB MAMC_GET_PROC_NA_PRJ
	 */
	public Short mamcGetProcNaPrj() throws ApplicationBusinessException{
		AghParametros par = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_SEQ_PROC_NA_PRJ);
		return par.getVlrNumerico().shortValue();
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	/**
	 * @throws ApplicationBusinessException 
	 * @ORADB MAMC_GET_PROC_DIAG
	 */
	public Short mamcGetProcDiag() throws ApplicationBusinessException{
		AghParametros par = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_SEQ_PROC_DIAG);
		return par.getVlrNumerico().shortValue();
	}

}
