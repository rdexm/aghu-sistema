package br.gov.mec.aghu.compras.pac.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoPropostaFornecedorDAO;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoPropostaFornecedorRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ScoPropostaFornecedorRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoPropostaFornecedorDAO scoPropostaFornecedorDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -893064524258218268L;

	public enum ScoPropostaFornecedorRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ITEMPROPOSTA_MSG010;
	}
	
	private void preInserirPropostaFornecedor(ScoPropostaFornecedor proposta) throws ApplicationBusinessException {
		if (verificarFornecedorPropostaAtivaPorLicitacao(proposta.getId().getLctNumero(), proposta.getId().getFrnNumero())) {
			throw new ApplicationBusinessException(
					ScoPropostaFornecedorRNExceptionCode.MENSAGEM_ITEMPROPOSTA_MSG010);	
		}
	}

	private void posInserirPropostaFornecedor(ScoPropostaFornecedor proposta) throws ApplicationBusinessException {
		//
	}
	
	public void inserirPropostaFornecedor(ScoPropostaFornecedor proposta) throws ApplicationBusinessException {
		this.preInserirPropostaFornecedor(proposta);
		this.getScoPropostaFornecedorDAO().persistir(proposta);
		this.posInserirPropostaFornecedor(proposta);
	}
	
	private void preAlterarPropostaFornecedor(ScoPropostaFornecedor proposta) throws ApplicationBusinessException {
		//
	}

	private void posAlterarPropostaFornecedor(ScoPropostaFornecedor proposta) throws ApplicationBusinessException {
		//
	}
	
	public void alterarPropostaFornecedor(ScoPropostaFornecedor proposta) throws ApplicationBusinessException {
		this.preAlterarPropostaFornecedor(proposta);
		this.getScoPropostaFornecedorDAO().persistir(proposta);
		this.posAlterarPropostaFornecedor(proposta);
	}

	private void preExcluirPropostaFornecedor(ScoPropostaFornecedor proposta) throws ApplicationBusinessException {
		//
	}

	private void posExcluirPropostaFornecedor(ScoPropostaFornecedor proposta) throws ApplicationBusinessException {
	//	
	}
	
	@SuppressWarnings("ucd")
	public void excluirPropostaFornecedor(ScoPropostaFornecedor proposta) throws ApplicationBusinessException {
		this.preExcluirPropostaFornecedor(proposta);
		this.getScoPropostaFornecedorDAO().remover(proposta);
		this.posExcluirPropostaFornecedor(proposta);
	}
	
	public Boolean verificarFornecedorPropostaAtivaPorLicitacao(Integer numeroPac, Integer numeroFornecedor) {
		return this.getScoPropostaFornecedorDAO().verificarFornecedorPropostaAtivaPorLicitacao(numeroPac, numeroFornecedor);
	}
	
	protected ScoPropostaFornecedorDAO getScoPropostaFornecedorDAO() {
		return scoPropostaFornecedorDAO;
	}

}