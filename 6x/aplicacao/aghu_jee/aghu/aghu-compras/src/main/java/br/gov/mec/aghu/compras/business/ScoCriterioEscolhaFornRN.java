package br.gov.mec.aghu.compras.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoCriterioEscolhaFornDAO;
import br.gov.mec.aghu.model.ScoCriterioEscolhaForn;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public  class ScoCriterioEscolhaFornRN extends BaseBusiness {

	private static final long serialVersionUID = 7995916805880345729L;

	public enum ScoCriterioEscolhaFornRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_CODIGO;
	}
	
	private static final Log LOG = LogFactory.getLog(ScoCriterioEscolhaFornRN.class);
	
	@Inject
	private ScoCriterioEscolhaFornDAO scoCriterioEscolhaFornDAO;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	public void inserir(ScoCriterioEscolhaForn criterioEscolhaForn) throws ApplicationBusinessException {
		
//		if(getScoCriterioEscolhaFornDAO().existeAcessoFornecedorPorFornecedor(acessoFornecedor.getScoFornecedor())) {
//			throw new ApplicationBusinessException(AcessoFornecedorRNExceptionCode.MENSAGEM_SENHA_ACESSO_CADASTRADA_FORNECEDOR);
//		}
		getScoCriterioEscolhaFornDAO().persistir(criterioEscolhaForn);
	//	getScoProgrCodAcessoFornDAO().flush();
	}

		
	public void atualizar(ScoCriterioEscolhaForn criterioEscolhaForn) throws ApplicationBusinessException {
		
		getScoCriterioEscolhaFornDAO().atualizar(criterioEscolhaForn);
	//	getScoProgrCodAcessoFornDAO().flush();
	}

	protected ScoCriterioEscolhaFornDAO getScoCriterioEscolhaFornDAO() {
		return scoCriterioEscolhaFornDAO;
	}
}
