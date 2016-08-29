package br.gov.mec.aghu.transplante.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.transplante.vo.CriteriosPriorizacaoAtendVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MtxCriterioPriorizacaoTmoON extends BaseBusiness {


	private static final long serialVersionUID = -32037798808761768L;

	private enum MtxCriterioPriorizacaoTmoONExceptionCode implements BusinessExceptionCode {
		CAMPO_CRITICIDADE_TMO_OBRIGATORIO,
		CAMPO_GRAVIDADE_TMO_OBRIGATORIO,
		CAMPO_CID_TMO_OBRIGATORIO,
		CAMPO_TIPO_TMO_ORBIGATORIO,
		CAMPO_STATUS_TMO_OBRIGATORIO;
	}

	@Override
	protected Log getLogger() {
		return null;
	}
	
	public void validarCamposObrigatorios(CriteriosPriorizacaoAtendVO filtro) throws ApplicationBusinessException, BaseListException {
		
		BaseListException lista = new BaseListException();

		if(filtro != null && filtro.getGravidade() == null){
			lista.add(new ApplicationBusinessException(MtxCriterioPriorizacaoTmoONExceptionCode.CAMPO_GRAVIDADE_TMO_OBRIGATORIO));
		}		
		if(filtro != null && filtro.getCriticidade() == null){
			lista.add(new ApplicationBusinessException(MtxCriterioPriorizacaoTmoONExceptionCode.CAMPO_CRITICIDADE_TMO_OBRIGATORIO));
		} 
		if(filtro != null && filtro.getTipoTmo() == null){
			lista.add(new ApplicationBusinessException(MtxCriterioPriorizacaoTmoONExceptionCode.CAMPO_TIPO_TMO_ORBIGATORIO));
		} 
		if(filtro != null && filtro.getStatus() == null){
			lista.add(new ApplicationBusinessException(MtxCriterioPriorizacaoTmoONExceptionCode.CAMPO_STATUS_TMO_OBRIGATORIO));
		} 

		if(lista.hasException()){
			throw lista;
		}
	}
}
