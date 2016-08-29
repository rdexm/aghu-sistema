package br.gov.mec.aghu.estoque.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.dao.SceDevolucaoFornecedorDAO;
import br.gov.mec.aghu.model.SceDevolucaoFornecedor;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class SceDevolucaoFornecedorRN extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(SceDevolucaoFornecedorRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceDevolucaoFornecedorDAO sceDevolucaoFornecedorDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6553806962142469696L;
	
	private void preInserir(SceDevolucaoFornecedor devolucaoFornecedor) throws ApplicationBusinessException {
		//
	}
	
	private void posInserir(SceDevolucaoFornecedor devolucaoFornecedor) throws ApplicationBusinessException {
		//
	}
	
	public void inserir(SceDevolucaoFornecedor devolucaoFornecedor, Boolean flush) throws ApplicationBusinessException {
		this.preInserir(devolucaoFornecedor);
		this.getSceDevolucaoFornecedorDAO().persistir(devolucaoFornecedor);
		
		if (flush) {
			// no processo de confirmacao da devolucao, o flush torna-se necessario pois
			// subsequentes selects na base sao feitos relacionando a devolucao do fornecedor
			// com outras entidades e como o hibernate ainda nao executou o insert na base,
			// nao traz nada.
			this.getSceDevolucaoFornecedorDAO().flush();
		}
		this.posInserir(devolucaoFornecedor);
	}
	
	private void preAlterar(SceDevolucaoFornecedor devolucaoFornecedor) throws ApplicationBusinessException {
		//
	}
	
	private void posAlterar(SceDevolucaoFornecedor devolucaoFornecedor) throws ApplicationBusinessException {
		//
	}

	@SuppressWarnings("ucd")
	public void alterar(SceDevolucaoFornecedor devolucaoFornecedor) throws ApplicationBusinessException {
		this.preAlterar(devolucaoFornecedor);
		this.getSceDevolucaoFornecedorDAO().persistir(devolucaoFornecedor);
		this.posAlterar(devolucaoFornecedor);
	}
	
	private void preExcluir(SceDevolucaoFornecedor devolucaoFornecedor) throws ApplicationBusinessException {
		//
	}
	
	private void posExcluir(SceDevolucaoFornecedor devolucaoFornecedor) throws ApplicationBusinessException {
		//
	}

	@SuppressWarnings("ucd")
	public void excluir(SceDevolucaoFornecedor devolucaoFornecedor) throws ApplicationBusinessException {
		this.preExcluir(devolucaoFornecedor);
		this.getSceDevolucaoFornecedorDAO().remover(devolucaoFornecedor);
		this.posExcluir(devolucaoFornecedor);
	}
	

	protected SceDevolucaoFornecedorDAO getSceDevolucaoFornecedorDAO(){
		return sceDevolucaoFornecedorDAO;
	}
}
