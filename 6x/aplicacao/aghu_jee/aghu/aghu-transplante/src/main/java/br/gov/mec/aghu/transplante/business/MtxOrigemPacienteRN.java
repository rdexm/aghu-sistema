package br.gov.mec.aghu.transplante.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.model.MtxOrigens;
import br.gov.mec.aghu.transplante.dao.MtxOrigensDAO;
import br.gov.mec.aghu.transplante.dao.MtxTransplantesDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MtxOrigemPacienteRN extends BaseBusiness {

	private static final long serialVersionUID = -1246077491374078346L;
	@Inject
	private MtxOrigensDAO mtxOrigensDAO;
	@Inject 
	private MtxTransplantesDAO mtxTransplantesDAO;

	private enum OrigemPacienteONExceptionCode implements BusinessExceptionCode {
		ERRO_ORIGEM_RELACIONADA,
		REGISTRO_JA_EXISTENTE_TRANSPLANTE;
	}

	@Override
	protected Log getLogger() {
		return null;
	}
	
	public void validarExclusaoOrigemPaciente(MtxOrigens mtxOrigens) throws ApplicationBusinessException, BaseListException {
		
		Long result = mtxTransplantesDAO.pesquisarMtxOrigensEmMtxTransplantesCount(mtxOrigens);
		if(result > 0){
			throw new ApplicationBusinessException(OrigemPacienteONExceptionCode.ERRO_ORIGEM_RELACIONADA);
		}
	}
	
	public void validarInclusaoOrigemPaciente(MtxOrigens mtxOrigens) throws ApplicationBusinessException, BaseListException {
		Long result = mtxOrigensDAO.pesquisarMtxOrigensPorSituacaoDescCount(mtxOrigens, true);
		if(result > 0){
		  throw new ApplicationBusinessException(OrigemPacienteONExceptionCode.REGISTRO_JA_EXISTENTE_TRANSPLANTE);
		}
		
	}
}
