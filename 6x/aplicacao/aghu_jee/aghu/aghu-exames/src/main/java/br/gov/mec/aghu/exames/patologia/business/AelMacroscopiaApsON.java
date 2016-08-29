package br.gov.mec.aghu.exames.patologia.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelMacroscopiaAps;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AelMacroscopiaApsON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AelMacroscopiaApsON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IExamesPatologiaFacade examesPatologiaFacade;

	private static final long serialVersionUID = 5531200013255294828L;

	

	private enum AelMacroscopiaApsONExceptionCode implements BusinessExceptionCode {
		  AEL_02710;
	}
 
	/**
	 * ORADB: AELF_LAUDO_UNICO.BUT_CONCLUI_MACRO.WHEN_BUTTON_PRESSED
	 */
	public void concluirMacroscopiaAps(final AelExameAp exameAp, final AelMacroscopiaAps macroscopia) throws BaseException{
		
		if (DominioSituacaoExamePatologia.RE.equals(exameAp.getEtapasLaudo()) && (macroscopia == null || StringUtils.isEmpty(macroscopia.getMacroscopia()))) {
			throw new ApplicationBusinessException(AelMacroscopiaApsONExceptionCode.AEL_02710);
		}
			
		if (DominioSituacaoExamePatologia.RE.equals(exameAp.getEtapasLaudo())) {
			exameAp.setEtapasLaudo(DominioSituacaoExamePatologia.MC);
			getExamesPatologiaFacade().persistirAelExameAp(exameAp);
		
		} else if(DominioSituacaoExamePatologia.MC.equals(exameAp.getEtapasLaudo())){
			exameAp.setEtapasLaudo(DominioSituacaoExamePatologia.RE);
			getExamesPatologiaFacade().persistirAelExameAp(exameAp);
		}	
	}
	
	public void validarMacroscopiaPreenchida(final AelMacroscopiaAps macroscopia) throws ApplicationBusinessException {
		if (StringUtils.isEmpty(macroscopia.getMacroscopia())) {
			throw new ApplicationBusinessException(AelMacroscopiaApsONExceptionCode.AEL_02710);
		}
	}
	
	protected IExamesPatologiaFacade getExamesPatologiaFacade() {
		return this.examesPatologiaFacade;
	}
}
