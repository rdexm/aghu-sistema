package br.gov.mec.aghu.transplante.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.model.MtxColetaMaterialTmo;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class InformarDadoMaterialRecebidoTmoON extends BaseBusiness{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4339718265008125459L;

	private enum InformarDadoMaterialRecebidoTmoONExceptionCode implements BusinessExceptionCode {
		CAMPO_MATERIAL_TMO_OBRIGATORIO,
		CAMPO_VOLUME_TMO_OBRIGATORIO,
		CAMPO_PESO_TMO_OBRIGATORIO;
	}

	@Override
	protected Log getLogger() {
		return null;
	}
	
	public void validarCamposObrigatorios(MtxColetaMaterialTmo coletaMaterialTmo) throws ApplicationBusinessException, BaseListException {
		
		BaseListException lista = new BaseListException();
		
		if(coletaMaterialTmo != null && coletaMaterialTmo.getCodMaterial() == null){
			lista.add(new ApplicationBusinessException(InformarDadoMaterialRecebidoTmoONExceptionCode.CAMPO_MATERIAL_TMO_OBRIGATORIO));
		} 
		if(coletaMaterialTmo != null && coletaMaterialTmo.getVolume() == null){
			lista.add(new ApplicationBusinessException(InformarDadoMaterialRecebidoTmoONExceptionCode.CAMPO_VOLUME_TMO_OBRIGATORIO));
		}
		if(coletaMaterialTmo != null && coletaMaterialTmo.getPeso() == null){
			lista.add(new ApplicationBusinessException(InformarDadoMaterialRecebidoTmoONExceptionCode.CAMPO_PESO_TMO_OBRIGATORIO));
		}
		
		if(lista.hasException()){
			throw lista;
		}
	}

}
