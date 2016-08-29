package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Calendar;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class TipoFrequenciaAprazamentoRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(TipoFrequenciaAprazamentoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IParametroFacade parametroFacade;
	
	public enum TipoFrequenciaAprazamentoRNExceptionCode implements BusinessExceptionCode {
		RAP_00175
		, MPM_00680
		, MPM_00681
		;
	}

	 /**
	 * 
	 */
	private static final long serialVersionUID = -2783541425475179463L;

	/*
 	 * ORADB Procedure MPMK_RN_AFA.RN_MPMP_VER_DEL (P_OLD_CRIADO_EM) 
	 * @param mpmTipoFrequenciaAprazamrnyo
	 * @throws ApplicationBusinessException
	 */
	public void preDeleteTipoFrequenciaAprazamento(MpmTipoFrequenciaAprazamento entity)
			throws ApplicationBusinessException {

		AghParametros aghParametro = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_DIAS_PERM_DEL_MPM);
		if (aghParametro != null && aghParametro.getVlrNumerico() != null) {
			float diff = CoreUtil.diferencaEntreDatasEmDias(Calendar
					.getInstance().getTime(), entity.getCriadoEm());
			if (diff > aghParametro.getVlrNumerico().floatValue()) {
				throw new ApplicationBusinessException(
						TipoFrequenciaAprazamentoRNExceptionCode.MPM_00681);
			}
		} else {
			throw new ApplicationBusinessException(
					TipoFrequenciaAprazamentoRNExceptionCode.MPM_00680);
		}
	}	
	
	
	/*
 	 * ORADB Procedure MPMT_AFQ_BRI 
	 * @param mpmTipoFrequenciaAprazamento
	 * @throws ApplicationBusinessException
	 */	
	public void verificaMatricula(MpmTipoFrequenciaAprazamento entity) throws ApplicationBusinessException{
		if (entity.getServidor()==null || entity.getServidor().getId()==null ||
				entity.getServidor().getId().getMatricula()==null){
			throw new ApplicationBusinessException(TipoFrequenciaAprazamentoRNExceptionCode.RAP_00175);
		}		
	}
	
	public IParametroFacade getParametroFacade()  {
		return parametroFacade;
	}	
	
}