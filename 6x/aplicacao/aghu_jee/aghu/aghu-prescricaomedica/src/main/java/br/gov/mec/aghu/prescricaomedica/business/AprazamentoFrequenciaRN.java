package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioFormaCalculoAprazamento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmAprazamentoFrequencia;
import br.gov.mec.aghu.prescricaomedica.business.PrescricaoMedicaRN.PrescricaoMedicamentoExceptionCode;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AprazamentoFrequenciaRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AprazamentoFrequenciaRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


	/**
	 * 
	 */
	private static final long serialVersionUID = 4678103353677652112L;

	/*
 	 * ORADB Procedure MPMT_AFQ_BRI 
	 * @param mpmTipoFrequenciaAprazamento
	 * @throws ApplicationBusinessException
	 */	
	public void verificaMatricula(MpmAprazamentoFrequencia entity) throws ApplicationBusinessException{
		if (entity.getServidor()==null || entity.getServidor().getId()==null ||
				entity.getServidor().getId().getMatricula()==null){
			throw new ApplicationBusinessException(AprazamentoExceptionCode.RAP_00175);
		}		
	}
	

	/*
 	 * ORADB Procedure MPMK_AFQ_RN.RN_AFQP_VER_TP_FREQ
	 * @param mpmTipoFrequenciaAprazamento
	 * @throws ApplicationBusinessException
	 */	
	public void verificaTipoFrequenciaAprazamento(MpmAprazamentoFrequencia entity) throws ApplicationBusinessException{
		if (entity.getTipoFreqAprazamento()==null || entity.getTipoFreqAprazamento().getSeq()==null){
			throw new ApplicationBusinessException(PrescricaoMedicamentoExceptionCode.MPM_00749);
		}else if (entity.getTipoFreqAprazamento().getIndSituacao()==null || 
						entity.getTipoFreqAprazamento().getIndSituacao().equals(DominioSituacao.I)){
			throw new ApplicationBusinessException(AprazamentoExceptionCode.MPM_01199);
		}else if (!entity.getTipoFreqAprazamento().getIndFormaAprazamento().equals(DominioFormaCalculoAprazamento.V) 
					&& !entity.getTipoFreqAprazamento().getIndFormaAprazamento().equals(DominioFormaCalculoAprazamento.C)){
			throw new ApplicationBusinessException(AprazamentoExceptionCode.MPM_00795);			
		}		
	}
	
	public enum AprazamentoExceptionCode implements BusinessExceptionCode {
		MPM_01199,MPM_00795,RAP_00175;
	}

}