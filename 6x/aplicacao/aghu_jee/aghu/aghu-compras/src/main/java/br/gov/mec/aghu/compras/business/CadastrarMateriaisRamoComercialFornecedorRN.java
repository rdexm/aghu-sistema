package br.gov.mec.aghu.compras.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoFnRamoComerClasDAO;
import br.gov.mec.aghu.model.ScoFnRamoComerClas;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class CadastrarMateriaisRamoComercialFornecedorRN extends BaseBusiness {

	private static final long serialVersionUID = -3909414234582816144L;

	private static final Log LOG = LogFactory.getLog(CadastrarMateriaisRamoComercialFornecedorRN.class);

	@Inject
	private ScoFnRamoComerClasDAO scoFnRamoComerClasDAO;

	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	public enum CadastrarMateriaisRamoComercialFornecedorRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_CLASSIFICACAO_JA_CADASTRADA;
	}


	public void inserirScoFnRamoComerClas(ScoFnRamoComerClas scoFnRamoComerClas) throws ApplicationBusinessException {
		
		verificarScoFnRamoComerClasJaCadastrado(scoFnRamoComerClas);		
		getScoFnRamoComerClasDAO().persistir(scoFnRamoComerClas);
	}
	
	private void verificarScoFnRamoComerClasJaCadastrado(ScoFnRamoComerClas scoFnRamoComerClas) throws ApplicationBusinessException{
		
		ScoFnRamoComerClas scoFnRamoComerClasEncontrado = getScoFnRamoComerClasDAO().obterPorChavePrimaria(scoFnRamoComerClas.getId());
		
		if(scoFnRamoComerClasEncontrado != null){
			throw new ApplicationBusinessException(CadastrarMateriaisRamoComercialFornecedorRNExceptionCode.MENSAGEM_CLASSIFICACAO_JA_CADASTRADA);
		}		
	}

	protected ScoFnRamoComerClasDAO getScoFnRamoComerClasDAO(){
		return scoFnRamoComerClasDAO;
	}	
	
}
