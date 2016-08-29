package br.gov.mec.aghu.exames.patologia.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelDiagnosticoAps;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelLaminaAps;
import br.gov.mec.aghu.model.AelNomenclaturaAps;
import br.gov.mec.aghu.model.AelTopografiaAps;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AelDiagnosticoApsON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AelDiagnosticoApsON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IExamesPatologiaFacade examesPatologiaFacade;

	private static final long serialVersionUID = -3830934052332583059L;

	private enum AelDiagnosticoApsONExceptionCode implements BusinessExceptionCode {
		  AEL_02711, AEL_02738, AEL_02739, MSG_PREENCHER_INDICE_BLOCOS
		;
	}
 
	/**
	 * ORADB: AELF_LAUDO_UNICO.BUT_CONCLUI_DIAG.WHEN_BUTTON_PRESSED
	 */
	public void concluirDiagnosticoAps(final AelExameAp exameAp, final AelDiagnosticoAps diagnostico, 
									   final List<AelTopografiaAps> listaTopografiaAp,
									   final List<AelNomenclaturaAps> listaNomenclaturaAp,
									   final List<AelLaminaAps> listaLaminaAp) throws BaseException{
		
		if(DominioSituacaoExamePatologia.RE.equals(exameAp.getEtapasLaudo()) || 
				DominioSituacaoExamePatologia.MC.equals(exameAp.getEtapasLaudo())){
			
		//	if(StringUtils.isEmpty(diagnostico.getDiagnostico())){
		//		throw new ApplicationBusinessException(AelDiagnosticoApsONExceptionCode.AEL_02711);
		//	}
			
			if(listaTopografiaAp == null || listaTopografiaAp.isEmpty()){
				throw new ApplicationBusinessException(AelDiagnosticoApsONExceptionCode.AEL_02738);
			}
			
			if(listaNomenclaturaAp == null || listaNomenclaturaAp.isEmpty()){
				throw new ApplicationBusinessException(AelDiagnosticoApsONExceptionCode.AEL_02739);
			}
			
			if (listaLaminaAp == null || listaLaminaAp.isEmpty()) { 
				throw new ApplicationBusinessException(AelDiagnosticoApsONExceptionCode.MSG_PREENCHER_INDICE_BLOCOS); //foi renomeado pra indice blocos
			}
		}
		
		if(DominioSituacaoExamePatologia.RE.equals(exameAp.getEtapasLaudo()) || 
				DominioSituacaoExamePatologia.MC.equals(exameAp.getEtapasLaudo())){
			
			exameAp.setEtapasLaudo(DominioSituacaoExamePatologia.DC);
			getExamesPatologiaFacade().persistirAelExameAp(exameAp);
			
		} else if(DominioSituacaoExamePatologia.DC.equals(exameAp.getEtapasLaudo())){
			// open c_config (:lux.lu2_seq);
			final AelConfigExLaudoUnico config = exameAp.getConfigExLaudoUnico();

			if(config != null && config.getMacro()){
				exameAp.setEtapasLaudo(DominioSituacaoExamePatologia.MC);
			} else {
				exameAp.setEtapasLaudo(DominioSituacaoExamePatologia.RE);
			}
			
			getExamesPatologiaFacade().persistirAelExameAp(exameAp);
		}
		
		getExamesPatologiaFacade().persistirAelDiagnosticoAps(diagnostico);
	}
	
//	public void validarDiagnosticoPreenchido(final AelDiagnosticoAps diagnostico) throws ApplicationBusinessException {
//		if (StringUtils.isEmpty(diagnostico.getDiagnostico())) {
//			throw new ApplicationBusinessException(AelDiagnosticoApsONExceptionCode.AEL_02711);
//		}
//	}	
	
	protected IExamesPatologiaFacade getExamesPatologiaFacade() {
		return this.examesPatologiaFacade;
	}
}
