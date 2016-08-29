package br.gov.mec.aghu.estoque.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.dao.SceConversaoUnidadeConsumosDAO;
import br.gov.mec.aghu.model.SceConversaoUnidadeConsumos;
import br.gov.mec.aghu.model.SceConversaoUnidadeConsumosId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class SceConversaoUnidadeConsumosRN extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(SceConversaoUnidadeConsumosRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceConversaoUnidadeConsumosDAO sceConversaoUnidadeConsumosDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -115049769079848697L;

	public enum SceConversaoUnidadeConsumosRNExceptionCode implements BusinessExceptionCode {
		MSG_REGISTRO_EXISTENTE_CONVERSAO;
		
		public void throwException(Object... params)throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}
	
	public void persistir(SceConversaoUnidadeConsumos conversao) throws ApplicationBusinessException{
		verificarExistenciaConversao(conversao);
		conversao.setCriadoEm(new Date());
		getSceConversaoUnidadeConsumosDAO().persistir(conversao);
		getSceConversaoUnidadeConsumosDAO().flush();
	}
	
	public void atualizar(SceConversaoUnidadeConsumos conversao){
		getSceConversaoUnidadeConsumosDAO().atualizar(conversao);
		getSceConversaoUnidadeConsumosDAO().flush();
	}
	
	public void excluir(SceConversaoUnidadeConsumosId conversaoId){
		SceConversaoUnidadeConsumos existente = getSceConversaoUnidadeConsumosDAO().obterPorChavePrimaria(conversaoId);
		getSceConversaoUnidadeConsumosDAO().remover(existente);
		getSceConversaoUnidadeConsumosDAO().flush();
	}		
	
	protected SceConversaoUnidadeConsumosDAO getSceConversaoUnidadeConsumosDAO() {
		return sceConversaoUnidadeConsumosDAO;
	}
	
	/**
	 * @ORADB SCET_CUD_BRI
	 * @param SceConversaoUnidadeConsumos
	 * @return
	 */
	public void verificarExistenciaConversao(SceConversaoUnidadeConsumos conversao) throws ApplicationBusinessException{
		SceConversaoUnidadeConsumos existente = getSceConversaoUnidadeConsumosDAO().obterPorChavePrimaria(conversao.getId());
		if(existente!=null){
			SceConversaoUnidadeConsumosRNExceptionCode.MSG_REGISTRO_EXISTENTE_CONVERSAO.throwException();
		}		
	}
}
