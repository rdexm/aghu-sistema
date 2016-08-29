package br.gov.mec.aghu.exames.patologia.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AelDescMaterialAps;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AelDescMaterialApsON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelDescMaterialApsON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;
	
	private static final long serialVersionUID = 4879378548867391256L;
	
	
	private enum AelDescMaterialApsONExceptionCode implements BusinessExceptionCode {
		AEL_02710_DESC_MATERIAL, MAX_LENGTH_DESC_MATERIAL;
	}
 
	public void concluirDescMaterialAps(final AelExameAp exameAp, final AelDescMaterialAps aelDescMaterialAps) throws BaseException {
		
		// TODO As regras abaixo seram revistas para todos os passos do laudo único, em outra estória.
		/*
		if (DominioSituacaoExamePatologia.RE.equals(exameAp.getEtapasLaudo()) && (aelDescMaterialAps == null || StringUtils.isEmpty(aelDescMaterialAps.getDescrMaterial()))) {
			throw new AGHUNegocioExceptionSemRollback(AelDescMaterialApsONExceptionCode.AEL_02710_DESC_MATERIAL);
		}
			
		if (DominioSituacaoExamePatologia.RE.equals(exameAp.getEtapasLaudo())) {
			exameAp.setEtapasLaudo(DominioSituacaoExamePatologia.MC);
			getExamesPatologiaFacade().persistirAelExameAp(exameAp, servidorLogado);
		
		} else if(DominioSituacaoExamePatologia.MC.equals(exameAp.getEtapasLaudo())){
			exameAp.setEtapasLaudo(DominioSituacaoExamePatologia.RE);
			getExamesPatologiaFacade().persistirAelExameAp(exameAp, servidorLogado);
		}
		*/	
	}
	
	public void validarDescMaterialPreenchida(final AelDescMaterialAps aelDescMaterialAps) throws ApplicationBusinessException {
		if (StringUtils.isEmpty(aelDescMaterialAps.getDescrMaterial())) {
			throw new ApplicationBusinessException(AelDescMaterialApsONExceptionCode.AEL_02710_DESC_MATERIAL);
		}
		if (aelDescMaterialAps.getDescrMaterial() != null && aelDescMaterialAps.getDescrMaterial().length() > 2000) {
			throw new ApplicationBusinessException(AelDescMaterialApsONExceptionCode.MAX_LENGTH_DESC_MATERIAL);
		}
	}
	
	protected IExamesPatologiaFacade getExamesPatologiaFacade() {
		return this.examesPatologiaFacade;
	}
	
}
